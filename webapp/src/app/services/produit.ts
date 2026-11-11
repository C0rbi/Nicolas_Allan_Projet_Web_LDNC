import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_URL } from '../config';
import { Categorie, Marque, Produit } from '../models/produit.model';

// Critères de recherche (tous facultatifs)
export interface FiltresProduits {
  recherche?: string;
  categorieId?: number | null;
  marqueId?: number | null;
  prixMax?: number | null;
  tri?: string; // '', 'prix-croissant', 'prix-decroissant' ou 'nouveautes'
}

@Injectable({
  providedIn: 'root',
})
export class ProduitService {
  private http = inject(HttpClient);
  private url = `${API_URL}/produits`;

  /** GET /api/produits?recherche=...&categorieId=... (seuls les filtres remplis sont envoyés) */
  rechercher(filtres: FiltresProduits = {}): Observable<Produit[]> {
    let params = new HttpParams();
    if (filtres.recherche) {
      params = params.set('recherche', filtres.recherche);
    }
    if (filtres.categorieId) {
      params = params.set('categorieId', filtres.categorieId);
    }
    if (filtres.marqueId) {
      params = params.set('marqueId', filtres.marqueId);
    }
    if (filtres.prixMax) {
      params = params.set('prixMax', filtres.prixMax);
    }
    if (filtres.tri) {
      params = params.set('tri', filtres.tri);
    }
    return this.http.get<Produit[]>(this.url, { params });
  }

  getProduit(id: number): Observable<Produit> {
    return this.http.get<Produit>(`${this.url}/${id}`);
  }

  getCategories(): Observable<Categorie[]> {
    return this.http.get<Categorie[]>(`${API_URL}/categories`);
  }

  getMarques(): Observable<Marque[]> {
    return this.http.get<Marque[]>(`${API_URL}/marques`);
  }

  // --- Administration (l'API vérifie que l'utilisateur a le rôle ADMIN) ---

  creer(produit: Produit): Observable<Produit> {
    return this.http.post<Produit>(this.url, produit);
  }

  modifier(id: number, produit: Produit): Observable<Produit> {
    return this.http.put<Produit>(`${this.url}/${id}`, produit);
  }

  supprimer(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
