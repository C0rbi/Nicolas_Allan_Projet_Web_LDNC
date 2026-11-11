package nc.allan.ldnc.service;

import lombok.RequiredArgsConstructor;
import nc.allan.ldnc.dto.DemandeConnexion;
import nc.allan.ldnc.dto.DemandeInscription;
import nc.allan.ldnc.dto.ReponseConnexion;
import nc.allan.ldnc.dto.UtilisateurDto;
import nc.allan.ldnc.entity.Role;
import nc.allan.ldnc.entity.Utilisateur;
import nc.allan.ldnc.repository.RoleRepository;
import nc.allan.ldnc.repository.UtilisateurRepository;
import nc.allan.ldnc.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/** Création de compte et connexion. */
@Service
@RequiredArgsConstructor   // Lombok : constructeur avec tous les attributs "final" (injection des dépendances)
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /** Crée un compte client puis le connecte directement. */
    public ReponseConnexion inscrire(DemandeInscription demande) {
        String email = demande.email().trim().toLowerCase();

        if (utilisateurRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Un compte existe déjà avec cet email");
        }

        Role roleClient = roleRepository.findByNom("CLIENT")
                .orElseThrow(() -> new IllegalStateException("Le rôle CLIENT est absent de la base"));

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setPrenom(demande.prenom().trim());
        utilisateur.setNom(demande.nom().trim());
        utilisateur.setEmail(email);
        utilisateur.setMotDePasse(passwordEncoder.encode(demande.motDePasse()));   // hachage BCrypt
        utilisateur.getRoles().add(roleClient);
        utilisateurRepository.save(utilisateur);

        return creerReponse(utilisateur);
    }

    /** Vérifie l'email et le mot de passe, puis renvoie un token. */
    public ReponseConnexion connecter(DemandeConnexion demande) {
        String email = demande.email().trim().toLowerCase();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email).orElse(null);

        // Même message si l'email est inconnu ou si le mot de passe est faux :
        // on ne révèle pas quels emails ont un compte
        if (utilisateur == null || !passwordEncoder.matches(demande.motDePasse(), utilisateur.getMotDePasse())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email ou mot de passe incorrect");
        }

        return creerReponse(utilisateur);
    }

    private ReponseConnexion creerReponse(Utilisateur utilisateur) {
        UtilisateurDto infos = UtilisateurDto.depuis(utilisateur);
        String token = jwtService.creerToken(infos.email(), infos.roles());
        return new ReponseConnexion(token, infos);
    }
}
