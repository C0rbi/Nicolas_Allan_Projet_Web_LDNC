import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService, NouveauCompte } from '../services/auth';

@Component({
  selector: 'app-compte-creation',
  imports: [FormsModule, RouterLink],
  templateUrl: './compte-creation.html',
  styleUrl: './compte-creation.css',
})
export class CompteCreation {
  private auth = inject(AuthService);
  private router = inject(Router);

  compte: NouveauCompte = { prenom: '', nom: '', email: '', motDePasse: '' };
  conditionsAcceptees = false;
  erreur = signal('');
  envoiEnCours = signal(false);

  creerCompte(): void {
    this.erreur.set('');
    this.envoiEnCours.set(true);
    // L'API crée le compte puis renvoie un token : l'utilisateur est directement connecté
    this.auth.inscription(this.compte).subscribe({
      next: () => this.router.navigate(['/']),
      error: (e) => {
        this.erreur.set(e.error?.message ?? 'Création du compte impossible : le serveur ne répond pas.');
        this.envoiEnCours.set(false);
      },
    });
  }
}
