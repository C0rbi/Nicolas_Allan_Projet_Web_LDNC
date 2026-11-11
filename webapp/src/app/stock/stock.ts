import { Component, input } from '@angular/core';

/**
 * Petit composant réutilisé sur plusieurs pages : affiche l'état du stock d'un produit.
 * Utilisation : <app-stock [stock]="produit.stock" />
 */
@Component({
  selector: 'app-stock',
  template: `
    @if (stock() === 0) {
      <span class="rupture">Rupture de stock</span>
    } @else if (stock() <= 5) {
      <span class="faible">Plus que {{ stock() }} en stock</span>
    } @else {
      <span class="disponible">En stock</span>
    }
  `,
  styles: `
    span { font-size: 0.9rem; font-weight: bold; }
    .disponible { color: var(--vert-stock); }
    .faible { color: var(--orange); }
    .rupture { color: var(--rouge); }
  `,
})
export class Stock {
  stock = input.required<number>();
}
