package nc.allan.ldnc;

import com.jayway.jsonpath.JsonPath;
import nc.allan.ldnc.entity.Produit;
import nc.allan.ldnc.entity.Role;
import nc.allan.ldnc.repository.ProduitRepository;
import nc.allan.ldnc.repository.RoleRepository;
import nc.allan.ldnc.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests d'intégration : l'API complète est démarrée (sécurité, contrôleurs, services, base H2)
 * et appelée comme le ferait le site Angular, grâce à MockMvc.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiTest {

    private static final String PRODUIT_JSON = """
            { "nom": "Produit de test", "prixHt": 10, "tva": 20, "stock": 3 }
            """;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ProduitRepository produitRepository;

    @BeforeEach
    void creerLesRoles() {
        for (String nom : List.of("ADMIN", "CLIENT")) {
            if (roleRepository.findByNom(nom).isEmpty()) {
                Role role = new Role();
                role.setNom(nom);
                roleRepository.save(role);
            }
        }
    }

    // ------------------------------------------------------------ Droits d'accès

    @Test
    void unVisiteur_peutVoirLeCatalogue() throws Exception {
        mockMvc.perform(get("/api/produits")).andExpect(status().isOk());
        mockMvc.perform(get("/api/categories")).andExpect(status().isOk());
    }

    @Test
    void unVisiteur_nePeutPasCreerDeProduit() throws Exception {
        mockMvc.perform(post("/api/produits").contentType(MediaType.APPLICATION_JSON).content(PRODUIT_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void unClient_nePeutPasCreerDeProduit() throws Exception {
        String token = jwtService.creerToken("client@test.nc", List.of("CLIENT"));

        mockMvc.perform(post("/api/produits").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(PRODUIT_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void unAdmin_peutCreerUnProduit() throws Exception {
        String token = jwtService.creerToken("admin@test.nc", List.of("ADMIN"));

        mockMvc.perform(post("/api/produits").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(PRODUIT_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nom").value("Produit de test"))
                .andExpect(jsonPath("$.prixTtc").value(12.0));
    }

    @Test
    void unProduitInvalide_estRefuseAvecUnMessage() throws Exception {
        String token = jwtService.creerToken("admin@test.nc", List.of("ADMIN"));

        mockMvc.perform(post("/api/produits").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                { "nom": "", "prixHt": 10, "tva": 20, "stock": 3 }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Le nom est obligatoire"));
    }

    @Test
    void lesStatistiques_sontReserveesAuxAdministrateurs() throws Exception {
        String tokenClient = jwtService.creerToken("client@test.nc", List.of("CLIENT"));

        mockMvc.perform(get("/api/statistiques")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/statistiques").header("Authorization", "Bearer " + tokenClient))
                .andExpect(status().isForbidden());
    }

    @Test
    void unVisiteur_nePeutPasVoirDePanier() throws Exception {
        mockMvc.perform(get("/api/panier")).andExpect(status().isUnauthorized());
    }

    @Test
    void unTokenModifie_estRefuse() throws Exception {
        String token = jwtService.creerToken("admin@test.nc", List.of("ADMIN")) + "x";

        mockMvc.perform(delete("/api/produits/1").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    // ------------------------------------------------------------ Comptes

    @Test
    void inscriptionPuisConnexion() throws Exception {
        String compte = """
                { "prenom": "Jean", "nom": "Martin", "email": "jean@test.nc", "motDePasse": "MotDePasse2026" }
                """;

        mockMvc.perform(post("/api/auth/inscription").contentType(MediaType.APPLICATION_JSON).content(compte))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.utilisateur.roles[0]").value("CLIENT"))
                .andExpect(jsonPath("$.utilisateur.motDePasse").doesNotExist());

        // Le même email ne peut pas être utilisé deux fois
        mockMvc.perform(post("/api/auth/inscription").contentType(MediaType.APPLICATION_JSON).content(compte))
                .andExpect(status().isConflict());

        mockMvc.perform(post("/api/auth/connexion").contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"email\": \"jean@test.nc\", \"motDePasse\": \"MotDePasse2026\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.utilisateur.prenom").value("Jean"));

        mockMvc.perform(post("/api/auth/connexion").contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"email\": \"jean@test.nc\", \"motDePasse\": \"mauvais\" }"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void inscription_avecUnMotDePasseTropCourt_estRefusee() throws Exception {
        mockMvc.perform(post("/api/auth/inscription").contentType(MediaType.APPLICATION_JSON).content("""
                        { "prenom": "Léa", "nom": "Petit", "email": "lea@test.nc", "motDePasse": "1234" }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Le mot de passe doit contenir entre 8 et 72 caractères"));
    }

    // ------------------------------------------------------------ Panier

    @Test
    void parcoursDuPanier() throws Exception {
        Produit produit = new Produit();
        produit.setNom("Souris de test");
        produit.setPrixHt(50.0);
        produit.setTva(20.0);
        produit.setStock(10);
        Integer id = produitRepository.save(produit).getId();

        String reponse = mockMvc.perform(post("/api/auth/inscription").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "prenom": "Paul", "nom": "Durand", "email": "paul@test.nc", "motDePasse": "MotDePasse2026" }
                                """))
                .andReturn().getResponse().getContentAsString();
        String token = "Bearer " + JsonPath.read(reponse, "$.token");

        // Ajout de 2 souris : 2 x 60 € TTC
        mockMvc.perform(post("/api/panier/" + id).param("quantite", "2").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreArticles").value(2))
                .andExpect(jsonPath("$.totalTtc").value(120.0));

        // Le panier est bien enregistré en base
        mockMvc.perform(get("/api/panier").header("Authorization", token))
                .andExpect(jsonPath("$.lignes[0].produit.nom").value("Souris de test"))
                .andExpect(jsonPath("$.lignes[0].quantite").value(2));

        mockMvc.perform(put("/api/panier/" + id).param("quantite", "5").header("Authorization", token))
                .andExpect(jsonPath("$.nombreArticles").value(5));

        mockMvc.perform(put("/api/panier/" + id).param("quantite", "11").header("Authorization", token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Stock insuffisant : il reste 10 exemplaire(s)"));

        mockMvc.perform(delete("/api/panier/" + id).header("Authorization", token))
                .andExpect(jsonPath("$.nombreArticles").value(0));
    }
}
