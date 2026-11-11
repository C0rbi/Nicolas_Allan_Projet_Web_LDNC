import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from './auth';

/**
 * Intercepteur : s'exécute avant chaque appel à l'API.
 * 1. Ajoute le token JWT dans l'en-tête "Authorization" si l'utilisateur est connecté.
 * 2. Si l'API répond 401 alors qu'un token était envoyé, le token a expiré : on déconnecte.
 */
export const authInterceptor: HttpInterceptorFn = (requete, suivant) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const token = auth.getToken();

  const requeteEnvoyee = token
    ? requete.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : requete;

  return suivant(requeteEnvoyee).pipe(
    catchError((erreur: HttpErrorResponse) => {
      if (erreur.status === 401 && token) {
        auth.deconnexion();
        router.navigate(['/compte'], { queryParams: { session: 'expiree' } });
      }
      return throwError(() => erreur);
    }),
  );
};
