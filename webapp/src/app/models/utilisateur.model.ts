export interface Utilisateur {
  id: number;
  prenom: string;
  nom: string;
  email: string;
  roles: string[]; // "ADMIN" et/ou "CLIENT"
}

// Réponse de l'API après une connexion ou une inscription réussie
export interface ReponseConnexion {
  token: string;
  utilisateur: Utilisateur;
}
