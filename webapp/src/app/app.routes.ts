import { Routes } from '@angular/router';
import { Accueil } from './accueil/accueil';
import { ProduitList } from './produit-list/produit-list';
import { ProduitDetail } from './produit-detail/produit-detail';
import { Compte } from './compte/compte';
import { CompteCreation } from './compte-creation/compte-creation';
import { MonPanier } from './mon-panier/mon-panier';
import { AdminProduits } from './admin-produits/admin-produits';
import { AdminProduitForm } from './admin-produit-form/admin-produit-form';
import { MentionsLegales } from './mentions-legales/mentions-legales';
import { adminGuard, connecteGuard } from './services/guards';

// "title" : texte affiché dans l'onglet du navigateur
export const routes: Routes = [
  { path: '', component: Accueil, title: 'LDNC - High-Tech Experience' },
  { path: 'produits', component: ProduitList, title: 'Nos produits - LDNC' },
  { path: 'produits/:id', component: ProduitDetail, title: 'Fiche produit - LDNC' },
  { path: 'compte', component: Compte, title: 'Mon compte - LDNC' },
  { path: 'compte-creation', component: CompteCreation, title: 'Créer un compte - LDNC' },
  { path: 'panier', component: MonPanier, canActivate: [connecteGuard], title: 'Mon panier - LDNC' },
  { path: 'admin', component: AdminProduits, canActivate: [adminGuard], title: 'Administration - LDNC' },
  { path: 'admin/produits/nouveau', component: AdminProduitForm, canActivate: [adminGuard], title: 'Nouveau produit - LDNC' },
  { path: 'admin/produits/:id', component: AdminProduitForm, canActivate: [adminGuard], title: 'Modifier un produit - LDNC' },
  { path: 'mentions-legales', component: MentionsLegales, title: 'Mentions légales - LDNC' },
  { path: '**', redirectTo: '' }, // adresse inconnue : retour à l'accueil
];
