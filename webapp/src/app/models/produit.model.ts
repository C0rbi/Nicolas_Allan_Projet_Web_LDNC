// Forme des données renvoyées par l'API (mêmes noms que dans les classes Java)

export interface Marque {
  id: number;
  nom: string;
}

export interface Categorie {
  id: number;
  nom: string;
  parentId: number | null; // null pour une catégorie principale (ex. Composants)
}

export interface Produit {
  id?: number; // absent tant que le produit n'est pas enregistré
  nom: string;
  description: string;
  prixHt: number;
  tva: number; // taux en %
  prixTtc?: number; // calculé par l'API
  stock: number;
  imageUrl: string;
  marque: Marque | null;
  categorie: Categorie | null;
  dateAjout?: string;
}
