import { Component, OnDestroy, inject, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Produit } from '../models/produit.model';
import { ProduitService } from '../services/produit';
import { Stock } from '../stock/stock';

@Component({
  selector: 'app-accueil',
  imports: [RouterLink, CurrencyPipe, Stock],
  templateUrl: './accueil.html',
  styleUrl: './accueil.css',
})
export class Accueil implements OnDestroy {
  private produitService = inject(ProduitService);

  // Images du carrousel (dossier public/carouselle/index)
  readonly images = [
    'carouselle/index/img0.jpg',
    'carouselle/index/img1.jpg',
    'carouselle/index/img2.jpg',
    'carouselle/index/img3.jpg',
    'carouselle/index/img4.jpg',
  ];
  indexImage = signal(0);
  nouveautes = signal<Produit[]>([]);

  // Le carrousel change d'image toutes les 5 secondes
  private minuteur = setInterval(() => this.suivante(), 5000);

  constructor() {
    // Les 8 produits ajoutés le plus récemment
    this.produitService.rechercher({ tri: 'nouveautes' }).subscribe((produits) => {
      this.nouveautes.set(produits.slice(0, 8));
    });
  }

  suivante(): void {
    this.indexImage.update((i) => (i + 1) % this.images.length);
  }

  precedente(): void {
    this.indexImage.update((i) => (i - 1 + this.images.length) % this.images.length);
  }

  afficher(index: number): void {
    this.indexImage.set(index);
  }

  ngOnDestroy(): void {
    clearInterval(this.minuteur); // arrête le minuteur quand on quitte la page
  }
}
