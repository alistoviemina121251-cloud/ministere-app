import { Component } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { AuthService } from './services/auth.service';

@Component({
  selector: 'app-root',
  template: `
    <div class="app-container">
      <header *ngIf="authService.isLoggedIn()" class="app-header">
        <div class="header-content">
          <div class="logo-section">
            <h1>📁 Ministère App</h1>
          </div>
          <nav class="nav-links">
            <a routerLink="/dashboard" routerLinkActive="active">Dashboard</a>
            <a routerLink="/marches" routerLinkActive="active">Marchés</a>
          </nav>
          <div class="user-section">
            <span class="user-info">👤 {{ authService.getCurrentUser()?.nom }}</span>
            <button class="btn btn-danger" (click)="logout()">Déconnexion</button>
          </div>
        </div>
      </header>
      <main class="main-content">
        <router-outlet></router-outlet>
      </main>
    </div>
  `,
  styles: [`
    .app-container { min-height: 100vh; background: #f4f7fc; }
    .app-header {
      background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%);
      color: white;
      padding: 1rem 2rem;
      box-shadow: 0 4px 6px rgba(0,0,0,0.1);
    }
    .header-content {
      max-width: 1400px;
      margin: 0 auto;
      display: flex;
      justify-content: space-between;
      align-items: center;
      flex-wrap: wrap;
    }
    .logo-section h1 { font-size: 1.5rem; margin: 0; }
    .nav-links { display: flex; gap: 2rem; }
    .nav-links a {
      color: rgba(255,255,255,0.8);
      text-decoration: none;
      padding: 0.5rem 1rem;
      border-radius: 4px;
      transition: all 0.3s;
    }
    .nav-links a:hover, .nav-links a.active {
      background: rgba(255,255,255,0.2);
      color: white;
    }
    .user-section { display: flex; align-items: center; gap: 1rem; }
    .user-info { color: rgba(255,255,255,0.9); }
    .btn-danger {
      background: #dc3545;
      color: white;
      border: none;
      padding: 0.6rem 1.2rem;
      border-radius: 4px;
      cursor: pointer;
    }
    .btn-danger:hover { background: #c82333; }
    .main-content { max-width: 1400px; margin: 2rem auto; padding: 0 2rem; }
  `]
})
export class AppComponent {
  // Routes accessibles sans être connecté : ne jamais y forcer une redirection vers /login
  private publicRoutes = ['/login', '/inscription'];

  constructor(public authService: AuthService, private router: Router) {
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        const isPublicRoute = this.publicRoutes.some(route =>
          event.urlAfterRedirects.startsWith(route)
        );
        if (!isPublicRoute && !this.authService.isLoggedIn()) {
          this.router.navigate(['/login']);
        }
      }
    });
  }

  logout(): void {
    this.authService.logout().subscribe({
      next: () => this.router.navigate(['/login']),
      error: () => this.router.navigate(['/login'])
    });
  }
}