import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { API_URL } from '../config';
import { AuthService } from './auth';

// Tests du service de connexion : l'API est simulée par HttpTestingController
describe('AuthService', () => {
  let auth: AuthService;
  let http: HttpTestingController;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    auth = TestBed.inject(AuthService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify()); // aucune requête inattendue

  it("un visiteur n'est pas connecté", () => {
    expect(auth.estConnecte()).toBe(false);
    expect(auth.estAdmin()).toBe(false);
  });

  it('la connexion garde le token et reconnaît un administrateur', () => {
    auth.connexion('admin@ldnc.nc', 'secret').subscribe();

    const requete = http.expectOne(`${API_URL}/auth/connexion`);
    expect(requete.request.method).toBe('POST');
    expect(requete.request.body).toEqual({ email: 'admin@ldnc.nc', motDePasse: 'secret' });
    requete.flush({
      token: 'token-de-test',
      utilisateur: { id: 1, prenom: 'Admin', nom: 'LDNC', email: 'admin@ldnc.nc', roles: ['ADMIN'] },
    });
    // Après la connexion, le panier du compte est chargé
    http.expectOne(`${API_URL}/panier`).flush({ lignes: [], nombreArticles: 0, totalHt: 0, totalTtc: 0 });

    expect(auth.getToken()).toBe('token-de-test');
    expect(auth.estConnecte()).toBe(true);
    expect(auth.estAdmin()).toBe(true);
  });

  it('la déconnexion efface le token et les informations du compte', () => {
    localStorage.setItem('ldnc-token', 'token-de-test');

    auth.deconnexion();

    expect(auth.getToken()).toBeNull();
    expect(localStorage.getItem('ldnc-utilisateur')).toBeNull();
    expect(auth.estConnecte()).toBe(false);
  });
});
