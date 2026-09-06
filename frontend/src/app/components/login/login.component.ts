import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  loginForm: FormGroup;
  errorMessage = '';
  successMessage = '';
  isLoading = false;
  showForgotPassword = false;
  forgotEmail = '';
  resetToken = '';
  newPassword = '';
  resetStep: 'request' | 'reset' = 'request';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
    console.log('✅ LoginComponent chargé');
  }

  onSubmit(): void {
    console.log('🔵 Tentative de connexion');
    if (this.loginForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';
      const { email, password } = this.loginForm.value;
      this.authService.login(email, password).subscribe({
        next: () => {
          this.isLoading = false;
          console.log('✅ Connexion réussie');
          this.router.navigate(['/dashboard']);
        },
        error: (err) => {
          this.isLoading = false;
          console.error('❌ Erreur connexion:', err);
          this.errorMessage = err.error?.message || 'Email ou mot de passe incorrect';
        }
      });
    }
  }

  goToRegister(): void {
    console.log('🔵 Navigation vers inscription');
    this.router.navigate(['/inscription']);
  }

  openForgotPassword(): void {
    this.showForgotPassword = true;
    this.resetStep = 'request';
    this.forgotEmail = '';
    this.errorMessage = '';
    this.successMessage = '';
  }

  closeForgotPassword(): void {
    this.showForgotPassword = false;
    this.resetStep = 'request';
    this.forgotEmail = '';
    this.resetToken = '';
    this.newPassword = '';
    this.errorMessage = '';
    this.successMessage = '';
  }

  onForgotSubmit(): void {
    if (!this.forgotEmail) {
      this.errorMessage = 'Veuillez saisir votre email';
      return;
    }
    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';
    this.authService.forgotPassword(this.forgotEmail).subscribe({
      next: (response) => {
        this.isLoading = false;
        if (response.success) {
          this.successMessage = response.message;
          this.resetStep = 'reset';
        } else {
          this.errorMessage = response.message || 'Erreur';
        }
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = err.error?.message || 'Erreur lors de la demande';
      }
    });
  }

  onResetSubmit(): void {
    if (!this.resetToken || !this.newPassword || this.newPassword.length < 6) {
      this.errorMessage = 'Le token et le mot de passe (6 caractères min) sont requis';
      return;
    }
    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';
    this.authService.resetPassword(this.forgotEmail, this.resetToken, this.newPassword).subscribe({
      next: (response) => {
        this.isLoading = false;
        if (response.success) {
          this.successMessage = response.message;
          setTimeout(() => {
            this.closeForgotPassword();
          }, 3000);
        } else {
          this.errorMessage = response.message || 'Erreur';
        }
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = err.error?.message || 'Erreur lors de la réinitialisation';
      }
    });
  }
}
