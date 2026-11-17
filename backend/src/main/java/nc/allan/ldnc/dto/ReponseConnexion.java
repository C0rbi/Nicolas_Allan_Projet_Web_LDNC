package nc.allan.ldnc.dto;

/**
 * Réponse à une connexion ou une inscription réussie :
 * le token JWT (à renvoyer dans l'en-tête Authorization) et les infos du compte.
 */
public record ReponseConnexion(String token, UtilisateurDto utilisateur) {
}
