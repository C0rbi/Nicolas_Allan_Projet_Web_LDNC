import { Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { Categorie } from '../models/produit.model';
import { AuthService } from '../services/auth';
import { PanierService } from '../services/panier';
import { ProduitService } from '../services/produit';

@Component({
  selector: 'app-navbar',
  imports: [FormsModule, RouterLink, RouterLinkActive],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar {
  protected auth = inject(AuthService);
  protected panierService = inject(PanierService);
  private produitService = inject(ProduitService);
  private router = inject(Router);

  texteRecherche = '';
  private categories = signal<Categorie[]>([]);

  // Le menu affiche les sous-catégories (Processeurs, Écrans...)
  protected categoriesMenu = computed(() => this.categories().filter((c) => c.parentId !== null));

  constructor() {
    this.produitService.getCategories().subscribe((categories) => this.categories.set(categories));
  }

  /** Ouvre la liste des produits avec le texte recherché dans l'adresse : /produits?recherche=ryzen */
  rechercher(): void {
    this.router.navigate(['/produits'], {
      queryParams: { recherche: this.texteRecherche.trim() || null },
    });
  }
}
