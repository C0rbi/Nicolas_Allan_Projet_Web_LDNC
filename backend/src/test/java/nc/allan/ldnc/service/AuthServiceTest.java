package nc.allan.ldnc.service;

import nc.allan.ldnc.dto.DemandeConnexion;
import nc.allan.ldnc.dto.DemandeInscription;
import nc.allan.ldnc.dto.ReponseConnexion;
import nc.allan.ldnc.entity.Role;
import nc.allan.ldnc.entity.Utilisateur;
import nc.allan.ldnc.repository.RoleRepository;
import nc.allan.ldnc.repository.UtilisateurRepository;
import nc.allan.ldnc.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/** Tests unitaires de l'inscription et de la connexion (repositories simulés, vrai BCrypt). */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private JwtService jwtService;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private AuthService authService;

    @BeforeEach
    void preparer() {
        authService = new AuthService(utilisateurRepository, roleRepository, passwordEncoder, jwtService);
    }

    @Test
    void inscrire_avecUnEmailDejaUtilise_estRefuse() {
        when(utilisateurRepository.existsByEmail("marie@ldnc.nc")).thenReturn(true);
        DemandeInscription demande = new DemandeInscription("Marie", "Dupont", "marie@ldnc.nc", "MotDePasse2026");

        ResponseStatusException erreur = assertThrows(ResponseStatusException.class, () -> authService.inscrire(demande));

        assertEquals(HttpStatus.CONFLICT, erreur.getStatusCode());
        verify(utilisateurRepository, never()).save(any());
    }

    @Test
    void inscrire_enregistreLeMotDePasseHacheEtLeRoleClient() {
        Role client = new Role();
        client.setNom("CLIENT");
        when(roleRepository.findByNom("CLIENT")).thenReturn(Optional.of(client));
        when(jwtService.creerToken(anyString(), anyList())).thenReturn("token-de-test");

        ReponseConnexion reponse = authService.inscrire(
                new DemandeInscription("Marie", "Dupont", " Marie@LDNC.nc ", "MotDePasse2026"));

        ArgumentCaptor<Utilisateur> capture = ArgumentCaptor.forClass(Utilisateur.class);
        verify(utilisateurRepository).save(capture.capture());
        Utilisateur enregistre = capture.getValue();

        assertEquals("marie@ldnc.nc", enregistre.getEmail());                          // email nettoyé
        assertNotEquals("MotDePasse2026", enregistre.getMotDePasse());                 // jamais en clair
        assertTrue(passwordEncoder.matches("MotDePasse2026", enregistre.getMotDePasse()));
        assertEquals(List.of("CLIENT"), reponse.utilisateur().roles());
        assertEquals("token-de-test", reponse.token());
    }

    @Test
    void connecter_avecUnMauvaisMotDePasse_estRefuse() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail("marie@ldnc.nc");
        utilisateur.setMotDePasse(passwordEncoder.encode("LeBonMotDePasse"));
        when(utilisateurRepository.findByEmail("marie@ldnc.nc")).thenReturn(Optional.of(utilisateur));

        ResponseStatusException erreur = assertThrows(ResponseStatusException.class,
                () -> authService.connecter(new DemandeConnexion("marie@ldnc.nc", "MauvaisMotDePasse")));

        assertEquals(HttpStatus.UNAUTHORIZED, erreur.getStatusCode());
    }

    @Test
    void connecter_avecUnEmailInconnu_donneLeMemeMessage() {
        when(utilisateurRepository.findByEmail("inconnu@ldnc.nc")).thenReturn(Optional.empty());

        ResponseStatusException erreur = assertThrows(ResponseStatusException.class,
                () -> authService.connecter(new DemandeConnexion("inconnu@ldnc.nc", "peuImporte")));

        assertEquals("Email ou mot de passe incorrect", erreur.getReason());
    }
}
