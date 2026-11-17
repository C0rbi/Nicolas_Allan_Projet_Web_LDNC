import { Component, computed, inject, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Produit } from '../models/produit.model';
import { AuthService } from '../services/auth';
import { PanierService } from '../services/panier';
import { ProduitService } from '../services/produit';
import { Stock } from '../stock/stock';

@Component({
  selector: 'app-produit-detail',
  imports: [FormsModule, RouterLink, CurrencyPipe, Stock],
  templateUrl: './produit-detail.html',
  styleUrl: './produit-detail.css',
})
export class ProduitDetail {
  private produitService = inject(ProduitService);
  private panierService = inject(PanierService);
  private auth = inject(AuthService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  produit = signal<Produit | null>(null);
  erreur = signal('');
  message = signal('');
  quantite = 1;

  // Choix de quantité proposés : de 1 au stock disponible (10 maximum)
  quantitesPossibles = computed(() => {
    const maximum = Math.min(this.produit()?.stock ?? 0, 10);
    return Array.from({ length: maximum }, (_, i) => i + 1);
  });

  constructor() {
    // L'id du produit est dans l'adresse : /produits/3
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.produitService.getProduit(id).subscribe({
      next: (produit) => this.produit.set(produit),
      error: () => this.erreur.set("Ce produit n'existe pas ou a été supprimé."),
    });
  }

  ajouterAuPanier(): void {
    if (!this.auth.estConnecte()) {
      this.router.navigate(['/compte'], { queryParams: { retour: this.router.url } });
      return;
    }
    this.erreur.set('');
    this.message.set('');
    this.panierService.ajouter(this.produit()!.id!, this.quantite).subscribe({
      next: () => this.message.set(`${this.quantite} article(s) ajouté(s) au panier.`),
      error: (e) => this.erreur.set(e.error?.message ?? "Impossible d'ajouter ce produit."),
    });
  }
}
