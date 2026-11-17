import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth';

// Les "guards" empêchent d'ouvrir une page. Ils améliorent l'affichage,
// mais la vraie protection est faite par l'API (SecurityConfig côté Spring).

/** Page réservée aux utilisateurs connectés : sinon, direction la page de connexion. */
export const connecteGuard: CanActivateFn = (route, etat) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  if (auth.estConnecte()) {
    return true;
  }
  return router.createUrlTree(['/compte'], { queryParams: { retour: etat.url } });
};

/** Page réservée aux administrateurs : sinon, retour à l'accueil. */
export const adminGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  return auth.estAdmin() ? true : router.createUrlTree(['/']);
};
