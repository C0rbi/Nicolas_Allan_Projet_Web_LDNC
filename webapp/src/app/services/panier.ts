import { Injectable, computed, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { API_URL } from '../config';
import { Panier } from '../models/panier.model';

/**
 * Panier de l'utilisateur connecté. Il est enregistré en base par l'API :
 * chaque méthode appelle l'API puis garde la réponse dans le signal "panier".
 */
@Injectable({
  providedIn: 'root',
})
export class PanierService {
  private http = inject(HttpClient);
  private url = `${API_URL}/panier`;

  /** Dernière version du panier reçue de l'API (null si personne n'est connecté). */
  readonly panier = signal<Panier | null>(null);

  /** Nombre d'articles, affiché sur l'icône du panier. Se met à jour tout seul. */
  readonly nombreArticles = computed(() => this.panier()?.nombreArticles ?? 0);

  charger(): Observable<Panier> {
    return this.http.get<Panier>(this.url).pipe(tap((panier) => this.panier.set(panier)));
  }

  ajouter(produitId: number, quantite = 1): Observable<Panier> {
    return this.http
      .post<Panier>(`${this.url}/${produitId}`, null, { params: { quantite } })
      .pipe(tap((panier) => this.panier.set(panier)));
  }

  modifierQuantite(produitId: number, quantite: number): Observable<Panier> {
    return this.http
      .put<Panier>(`${this.url}/${produitId}`, null, { params: { quantite } })
      .pipe(tap((panier) => this.panier.set(panier)));
  }

  retirer(produitId: number): Observable<Panier> {
    return this.http
      .delete<Panier>(`${this.url}/${produitId}`)
      .pipe(tap((panier) => this.panier.set(panier)));
  }

  vider(): Observable<Panier> {
    return this.http.delete<Panier>(this.url).pipe(tap((panier) => this.panier.set(panier)));
  }

  /** À la déconnexion : on oublie le panier affiché. */
  oublier(): void {
    this.panier.set(null);
  }
}
