import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'https://ministere-app.onrender.com/api/auth';
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  isAuthenticated$ = this.isAuthenticatedSubject.asObservable();
  private currentUserSubject = new BehaviorSubject<string>('');
  currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    const token = localStorage.getItem('authToken');
    const username = localStorage.getItem('username');
    if (token) {
      this.isAuthenticatedSubject.next(true);
      if (username) {
        this.currentUserSubject.next(username);
      }
    }
    console.log('✅ AuthService chargé, API URL:', this.apiUrl);
  }

  inscrire(userData: any): Observable<any> {
    console.log('📤🔴 ENVOI VERS:', `${this.apiUrl}/inscrire`);
    console.log('📤🔴 DONNÉES:', userData);
    return this.http.post(`${this.apiUrl}/inscrire`, userData);
  }

  confirmerCompte(email: string, code: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/confirmer`, { email, code });
  }

  renvoyerCode(email: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/renvoyer-code`, { email });
  }

  login(email: string, password: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, { email, password })
      .pipe(
        tap((response: any) => {
          if (response.success) {
            localStorage.setItem('authToken', response.token || 'dummy-token');
            localStorage.setItem('username', response.nomUtilisateur || email);
            this.isAuthenticatedSubject.next(true);
            this.currentUserSubject.next(response.nomUtilisateur || email);
          }
        })
      );
  }

  forgotPassword(email: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/forgot-password`, { email });
  }

  resetPassword(email: string, token: string, newPassword: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/reset-password`, { email, token, newPassword });
  }

  isLoggedIn(): boolean {
    return this.isAuthenticatedSubject.value;
  }

  getCurrentUser(): string {
    return this.currentUserSubject.value;
  }

  logout(): Observable<any> {
    return this.http.post(`${this.apiUrl}/logout`, {});
  }

  doLogout(): void {
    localStorage.removeItem('authToken');
    localStorage.removeItem('username');
    this.isAuthenticatedSubject.next(false);
    this.currentUserSubject.next('');
  }
}
