package nc.allan.ldnc.service;

import nc.allan.ldnc.entity.Panier;
import nc.allan.ldnc.entity.Produit;
import nc.allan.ldnc.repository.PanierRepository;
import nc.allan.ldnc.repository.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Tests unitaires des règles du panier.
 * Les repositories sont remplacés par des "mocks" (Mockito) : pas de base de données.
 */
@ExtendWith(MockitoExtension.class)
class PanierServiceTest {

    private static final String EMAIL = "client@ldnc.nc";

    @Mock
    private PanierRepository panierRepository;
    @Mock
    private UtilisateurRepository utilisateurRepository;
    @Mock
    private ProduitService produitService;

    @InjectMocks
    private PanierService panierService;

    private Panier panier;
    private Produit produit;

    @BeforeEach
    void preparer() {
        panier = new Panier();
        when(panierRepository.findByUtilisateurEmail(EMAIL)).thenReturn(Optional.of(panier));

        produit = new Produit();
        produit.setId(1);
        produit.setNom("AMD Ryzen 7 9800X3D");
        produit.setPrixHt(100.0);
        produit.setTva(20.0);
        produit.setStock(5);
    }

    @Test
    void ajouter_unNouveauProduit_creeUneLigne() {
        when(produitService.trouver(1)).thenReturn(produit);

        panierService.ajouter(EMAIL, 1, 2);

        assertEquals(1, panier.getLignes().size());
        assertEquals(2, panier.getNombreArticles());
        assertEquals(200.0, panier.getTotalHt());
        assertEquals(240.0, panier.getTotalTtc());
    }

    @Test
    void ajouter_unProduitDejaDansLePanier_augmenteLaQuantite() {
        when(produitService.trouver(1)).thenReturn(produit);

        panierService.ajouter(EMAIL, 1, 1);
        panierService.ajouter(EMAIL, 1, 2);

        assertEquals(1, panier.getLignes().size());
        assertEquals(3, panier.getLignes().getFirst().getQuantite());
    }

    @Test
    void ajouter_plusQueLeStock_estRefuse() {
        when(produitService.trouver(1)).thenReturn(produit);

        ResponseStatusException erreur = assertThrows(ResponseStatusException.class,
                () -> panierService.ajouter(EMAIL, 1, 6));

        assertEquals(HttpStatus.BAD_REQUEST, erreur.getStatusCode());
        assertTrue(panier.getLignes().isEmpty());
    }

    @Test
    void ajouter_unProduitEnRupture_estRefuse() {
        produit.setStock(0);
        when(produitService.trouver(1)).thenReturn(produit);

        ResponseStatusException erreur = assertThrows(ResponseStatusException.class,
                () -> panierService.ajouter(EMAIL, 1, 1));

        assertEquals("Ce produit est en rupture de stock", erreur.getReason());
    }

    @Test
    void modifierQuantite_aZero_estRefuse() {
        when(produitService.trouver(1)).thenReturn(produit);
        panierService.ajouter(EMAIL, 1, 2);

        assertThrows(ResponseStatusException.class, () -> panierService.modifierQuantite(EMAIL, 1, 0));
        assertEquals(2, panier.getNombreArticles());
    }

    @Test
    void retirer_supprimeLaLigne() {
        when(produitService.trouver(1)).thenReturn(produit);
        panierService.ajouter(EMAIL, 1, 2);

        panierService.retirer(EMAIL, 1);

        assertTrue(panier.getLignes().isEmpty());
        assertEquals(0.0, panier.getTotalTtc());
    }
}
