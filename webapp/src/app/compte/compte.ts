import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../services/auth';

@Component({
  selector: 'app-compte',
  imports: [FormsModule, RouterLink],
  templateUrl: './compte.html',
  styleUrl: './compte.css',
})
export class Compte {
  protected auth = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  email = '';
  motDePasse = '';
  erreur = signal('');
  envoiEnCours = signal(false);

  // Arrivée ici parce que le token a expiré (voir auth.interceptor.ts)
  sessionExpiree = this.route.snapshot.queryParamMap.get('session') === 'expiree';

  connexion(): void {
    this.erreur.set('');
    this.envoiEnCours.set(true);
    this.auth.connexion(this.email, this.motDePasse).subscribe({
      next: () => {
        // Retour à la page demandée avant la connexion (ex. le panier), sinon l'accueil
        const retour = this.route.snapshot.queryParamMap.get('retour') ?? '/';
        this.router.navigateByUrl(retour);
      },
      error: (e) => {
        this.erreur.set(e.error?.message ?? 'Connexion impossible : le serveur ne répond pas.');
        this.envoiEnCours.set(false);
      },
    });
  }

  deconnexion(): void {
    this.auth.deconnexion();
    this.router.navigate(['/']);
  }
}
