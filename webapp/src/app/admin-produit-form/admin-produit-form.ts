import { Component, inject, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Categorie, Marque, Produit } from '../models/produit.model';
import { ProduitService } from '../services/produit';

/**
 * Administration : formulaire d'ajout (/admin/produits/nouveau)
 * et de modification (/admin/produits/3) d'un produit.
 */
@Component({
  selector: 'app-admin-produit-form',
  imports: [FormsModule, RouterLink, DecimalPipe],
  templateUrl: './admin-produit-form.html',
  styleUrl: './admin-produit-form.css',
})
export class AdminProduitForm {
  private produitService = inject(ProduitService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  // id présent dans l'adresse = modification ; absent = nouveau produit
  readonly id = Number(this.route.snapshot.paramMap.get('id')) || null;

  produit = signal<Produit | null>(null);
  marques = signal<Marque[]>([]);
  categories = signal<Categorie[]>([]);
  erreur = signal('');
  envoiEnCours = signal(false);

  constructor() {
    this.produitService.getMarques().subscribe((marques) => this.marques.set(marques));
    this.produitService.getCategories().subscribe((categories) => this.categories.set(categories));

    if (this.id) {
      this.produitService.getProduit(this.id).subscribe({
        next: (produit) => this.produit.set(produit),
        error: () => this.erreur.set('Produit introuvable.'),
      });
    } else {
      this.produit.set({
        nom: '',
        description: '',
        prixHt: 0,
        tva: 20,
        stock: 0,
        imageUrl: '',
        marque: null,
        categorie: null,
      });
    }
  }

  /** Les listes déroulantes comparent la marque / la catégorie par leur id. */
  memeId(a: { id: number } | null, b: { id: number } | null): boolean {
    return a?.id === b?.id;
  }

  enregistrer(): void {
    this.erreur.set('');
    this.envoiEnCours.set(true);
    const produit = this.produit()!;
    const requete = this.id
      ? this.produitService.modifier(this.id, produit)
      : this.produitService.creer(produit);

    requete.subscribe({
      next: () => this.router.navigate(['/admin']),
      error: (e) => {
        this.erreur.set(e.error?.message ?? 'Enregistrement impossible.');
        this.envoiEnCours.set(false);
      },
    });
  }
}
