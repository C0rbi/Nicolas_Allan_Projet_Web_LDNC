package nc.allan.ldnc.dto;

import nc.allan.ldnc.entity.Role;
import nc.allan.ldnc.entity.Utilisateur;

import java.util.List;

/**
 * Informations du compte renvoyées au navigateur.
 * On n'envoie jamais l'entité Utilisateur directement : elle contient le mot de passe haché.
 */
public record UtilisateurDto(Integer id, String prenom, String nom, String email, List<String> roles) {

    public static UtilisateurDto depuis(Utilisateur utilisateur) {
        List<String> roles = utilisateur.getRoles().stream().map(Role::getNom).toList();
        return new UtilisateurDto(utilisateur.getId(), utilisateur.getPrenom(), utilisateur.getNom(),
                utilisateur.getEmail(), roles);
    }
}
