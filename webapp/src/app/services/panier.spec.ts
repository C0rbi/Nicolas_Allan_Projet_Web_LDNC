import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { API_URL } from '../config';
import { PanierService } from './panier';

describe('PanierService', () => {
  let service: PanierService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(PanierService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it("ajouter() appelle l'API puis met à jour le nombre d'articles", () => {
    expect(service.nombreArticles()).toBe(0);

    service.ajouter(3, 2).subscribe();

    const requete = http.expectOne((r) => r.url === `${API_URL}/panier/3`);
    expect(requete.request.method).toBe('POST');
    expect(requete.request.params.get('quantite')).toBe('2');
    requete.flush({ lignes: [], nombreArticles: 2, totalHt: 100, totalTtc: 120 });

    expect(service.nombreArticles()).toBe(2);
    expect(service.panier()?.totalTtc).toBe(120);
  });

  it('oublier() efface le panier affiché', () => {
    service.panier.set({ lignes: [], nombreArticles: 4, totalHt: 10, totalTtc: 12 });

    service.oublier();

    expect(service.panier()).toBeNull();
    expect(service.nombreArticles()).toBe(0);
  });
});
