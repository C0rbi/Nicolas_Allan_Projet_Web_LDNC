import { Component, inject, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { LignePanier } from '../models/panier.model';
import { PanierService } from '../services/panier';

@Component({
  selector: 'app-mon-panier',
  imports: [RouterLink, CurrencyPipe],
  templateUrl: './mon-panier.html',
  styleUrl: './mon-panier.css',
})
export class MonPanier {
  protected panierService = inject(PanierService);
  erreur = signal('');

  constructor() {
    // On recharge le panier pour avoir les prix et les stocks à jour
    this.panierService.charger().subscribe();
  }

  changerQuantite(ligne: LignePanier, quantite: number): void {
    this.erreur.set('');
    this.panierService.modifierQuantite(ligne.produit.id!, quantite).subscribe({
      error: (e) => this.erreur.set(e.error?.message ?? 'Modification impossible.'),
    });
  }

  retirer(ligne: LignePanier): void {
    this.erreur.set('');
    this.panierService.retirer(ligne.produit.id!).subscribe();
  }

  vider(): void {
    if (confirm('Voulez-vous vraiment vider votre panier ?')) {
      this.panierService.vider().subscribe();
    }
  }
}
