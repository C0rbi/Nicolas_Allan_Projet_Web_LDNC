import { Component, inject, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Categorie, Marque, Produit } from '../models/produit.model';
import { AuthService } from '../services/auth';
import { PanierService } from '../services/panier';
import { ProduitService } from '../services/produit';
import { Stock } from '../stock/stock';

@Component({
  selector: 'app-produit-list',
  imports: [FormsModule, RouterLink, CurrencyPipe, Stock],
  templateUrl: './produit-list.html',
  styleUrl: './produit-list.css',
})
export class ProduitList {
  private produitService = inject(ProduitService);
  private panierService = inject(PanierService);
  private auth = inject(AuthService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  produits = signal<Produit[]>([]);
  categories = signal<Categorie[]>([]);
  marques = signal<Marque[]>([]);
  chargement = signal(true);
  erreur = signal('');
  // Message affiché sous le bouton "Ajouter au panier" du produit concerné
  retour = signal<{ produitId: number; texte: string; estErreur: boolean } | null>(null);

  // Valeurs du formulaire de filtres (relues dans l'adresse de la page)
  filtres = {
    recherche: '',
    categorieId: null as number | null,
    marqueId: null as number | null,
    prixMax: null as number | null,
    tri: '',
  };

  constructor() {
    this.produitService.getCategories().subscribe((categories) => this.categories.set(categories));
    this.produitService.getMarques().subscribe((marques) => this.marques.set(marques));

    // À chaque changement de l'adresse (/produits?recherche=...&categorie=...),
    // on relit les filtres puis on recharge la liste
    this.route.queryParamMap.pipe(takeUntilDestroyed()).subscribe((params) => {
      this.filtres = {
        recherche: params.get('recherche') ?? '',
        categorieId: this.versNombre(params.get('categorie')),
        marqueId: this.versNombre(params.get('marque')),
        prixMax: this.versNombre(params.get('prixMax')),
        tri: params.get('tri') ?? '',
      };
      this.charger();
    });
  }

  private charger(): void {
    this.chargement.set(true);
    this.erreur.set('');
    this.produitService.rechercher(this.filtres).subscribe({
      next: (produits) => {
        this.produits.set(produits);
        this.chargement.set(false);
      },
      error: () => {
        this.erreur.set("Impossible de charger les produits : l'API est-elle démarrée ?");
        this.chargement.set(false);
      },
    });
  }

  /** Écrit les filtres dans l'adresse de la page : la liste se recharge alors toute seule. */
  appliquerFiltres(): void {
    this.router.navigate(['/produits'], {
      queryParams: {
        recherche: this.filtres.recherche.trim() || null,
        categorie: this.filtres.categorieId,
        marque: this.filtres.marqueId,
        prixMax: this.filtres.prixMax,
        tri: this.filtres.tri || null,
      },
    });
  }

  reinitialiser(): void {
    this.router.navigate(['/produits']);
  }

  ajouterAuPanier(produit: Produit): void {
    if (!this.auth.estConnecte()) {
      // Il faut un compte : après la connexion, retour sur cette page
      this.router.navigate(['/compte'], { queryParams: { retour: this.router.url } });
      return;
    }
    this.panierService.ajouter(produit.id!).subscribe({
      next: () => this.retour.set({ produitId: produit.id!, texte: 'Ajouté au panier', estErreur: false }),
      error: (e) => this.retour.set({ produitId: produit.id!, texte: e.error?.message ?? 'Ajout impossible', estErreur: true }),
    });
  }

  /** Nom de la catégorie choisie, pour le titre de la page. */
  nomCategorie(): string {
    return this.categories().find((c) => c.id === this.filtres.categorieId)?.nom ?? 'Nos produits';
  }

  private versNombre(valeur: string | null): number | null {
    return valeur ? Number(valeur) : null;
  }
}
