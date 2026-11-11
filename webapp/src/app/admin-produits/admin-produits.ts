import { Component, inject, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Produit, StockCategorie } from '../models/produit.model';
import { ProduitService } from '../services/produit';

/** Administration : liste des produits avec les boutons Modifier et Supprimer. */
@Component({
  selector: 'app-admin-produits',
  imports: [RouterLink, CurrencyPipe],
  templateUrl: './admin-produits.html',
  styleUrl: './admin-produits.css',
})
export class AdminProduits {
  private produitService = inject(ProduitService);

  produits = signal<Produit[]>([]);
  statistiques = signal<StockCategorie[]>([]);
  message = signal('');
  erreur = signal('');

  constructor() {
    this.charger();
  }

  charger(): void {
    this.produitService.rechercher().subscribe((produits) => this.produits.set(produits));
    this.produitService.getStatistiques().subscribe((lignes) => this.statistiques.set(lignes));
  }

  supprimer(produit: Produit): void {
    if (!confirm(`Supprimer « ${produit.nom} » ? Cette action est définitive.`)) {
      return;
    }
    this.message.set('');
    this.erreur.set('');
    this.produitService.supprimer(produit.id!).subscribe({
      next: () => {
        this.message.set(`« ${produit.nom} » a été supprimé.`);
        this.charger();
      },
      error: (e) => this.erreur.set(e.error?.message ?? 'Suppression impossible.'),
    });
  }
}
