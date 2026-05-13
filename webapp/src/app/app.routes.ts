import { Routes } from '@angular/router';
import { Compte } from './compte/compte';
import { IndexHero } from './index-hero/index-hero';
import { CompteCreation } from './compte-creation/compte-creation';
import { ProduitListComponent } from './components/produit-list/produit-list';

export const routes: Routes = [
    {path: '', component: IndexHero},
    {path: 'compte', component: Compte},
    {path: 'compte-creation', component: CompteCreation},
    {path: 'components/product-list', component: ProduitListComponent}
];