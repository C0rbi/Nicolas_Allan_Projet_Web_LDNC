import { Produit } from './produit.model';

export interface LignePanier {
  produit: Produit;
  quantite: number;
  sousTotalTtc: number;
}

// Les totaux sont calculés par l'API
export interface Panier {
  lignes: LignePanier[];
  nombreArticles: number;
  totalHt: number;
  totalTtc: number;
}
