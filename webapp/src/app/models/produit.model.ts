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

// Une ligne de la vue SQL vue_stock_par_categorie (mêmes noms que les colonnes de la vue)
export interface StockCategorie {
  categorie: string;
  nb_produits: number;
  nb_en_rupture: number;
  nb_articles: number;
  valeur_stock_ht: number;
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
