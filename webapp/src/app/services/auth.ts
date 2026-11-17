import { Injectable, computed, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { API_URL } from '../config';
import { ReponseConnexion, Utilisateur } from '../models/utilisateur.model';
import { PanierService } from './panier';

// Clés utilisées pour garder la connexion dans le navigateur (localStorage)
const CLE_TOKEN = 'ldnc-token';
const CLE_UTILISATEUR = 'ldnc-utilisateur';

export interface NouveauCompte {
  prenom: string;
  nom: string;
  email: string;
  motDePasse: string;
}

/**
 * Connexion, inscription et déconnexion.
 * Le token JWT reçu de l'API est gardé dans le localStorage : l'utilisateur
 * reste connecté s'il recharge la page (jusqu'à l'expiration du token).
 */
@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient);
  private panierService = inject(PanierService);

  /** Utilisateur connecté, ou null pour un visiteur. */
  readonly utilisateur = signal<Utilisateur | null>(this.lireUtilisateur());
  readonly estConnecte = computed(() => this.utilisateur() !== null);
  readonly estAdmin = computed(() => this.utilisateur()?.roles.includes('ADMIN') ?? false);

  connexion(email: string, motDePasse: string): Observable<ReponseConnexion> {
    return this.http
      .post<ReponseConnexion>(`${API_URL}/auth/connexion`, { email, motDePasse })
      .pipe(tap((reponse) => this.enregistrer(reponse)));
  }

  inscription(compte: NouveauCompte): Observable<ReponseConnexion> {
    return this.http
      .post<ReponseConnexion>(`${API_URL}/auth/inscription`, compte)
      .pipe(tap((reponse) => this.enregistrer(reponse)));
  }

  deconnexion(): void {
    localStorage.removeItem(CLE_TOKEN);
    localStorage.removeItem(CLE_UTILISATEUR);
    this.utilisateur.set(null);
    this.panierService.oublier();
  }

  getToken(): string | null {
    return localStorage.getItem(CLE_TOKEN);
  }

  private enregistrer(reponse: ReponseConnexion): void {
    localStorage.setItem(CLE_TOKEN, reponse.token);
    localStorage.setItem(CLE_UTILISATEUR, JSON.stringify(reponse.utilisateur));
    this.utilisateur.set(reponse.utilisateur);
    this.panierService.charger().subscribe(); // affiche tout de suite le panier du compte
  }

  private lireUtilisateur(): Utilisateur | null {
    const texte = localStorage.getItem(CLE_UTILISATEUR);
    return texte ? JSON.parse(texte) : null;
  }
}
