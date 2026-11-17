import { Component, OnInit, inject } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { Navbar } from './navbar/navbar';
import { AuthService } from './services/auth';
import { PanierService } from './services/panier';

@Component({
  selector: 'app-root',
  imports: [Navbar, RouterOutlet, RouterLink],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App implements OnInit {
  private auth = inject(AuthService);
  private panierService = inject(PanierService);

  ngOnInit(): void {
    // Utilisateur resté connecté (token gardé dans le navigateur) : on récupère son panier
    if (this.auth.estConnecte()) {
      this.panierService.charger().subscribe();
    }
  }
}
