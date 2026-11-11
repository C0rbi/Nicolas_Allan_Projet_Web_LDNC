import { ApplicationConfig, LOCALE_ID } from '@angular/core';
import { registerLocaleData } from '@angular/common';
import localeFr from '@angular/common/locales/fr';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideRouter, withInMemoryScrolling } from '@angular/router';
import { routes } from './app.routes';
import { authInterceptor } from './services/auth.interceptor';

// Prix et dates au format français : 1 234,56 €
registerLocaleData(localeFr);

export const appConfig: ApplicationConfig = {
  providers: [
    // scrollPositionRestoration : chaque nouvelle page s'affiche depuis le haut
    provideRouter(routes, withInMemoryScrolling({ scrollPositionRestoration: 'top' })),
    // L'intercepteur ajoute le token JWT à chaque appel de l'API
    provideHttpClient(withInterceptors([authInterceptor])),
    { provide: LOCALE_ID, useValue: 'fr' },
  ],
};
