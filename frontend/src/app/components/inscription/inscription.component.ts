import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-inscription',
  templateUrl: './inscription.component.html',
  styleUrls: ['./inscription.component.scss']
})
export class InscriptionComponent {
  inscriptionForm: FormGroup;
  isLoading = false;
  errorMessage = '';
  successMessage = '';
  step: 'form' | 'confirmation' = 'form';
  email = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    console.log('✅ InscriptionComponent CONSTRUIT');
    this.inscriptionForm = this.fb.group({
      nom: ['', Validators.required],
      prenom: ['', Validators.required],
      nomUtilisateur: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required]
    });
    console.log('✅ Formulaire créé');
  }

  onSubmit(): void {
    console.log('🔴🔴🔴🔴🔴 BOUTON S\'INSCRIRE CLIQUE ! 🔴🔴🔴🔴🔴');
    
    if (this.inscriptionForm.valid) {
      console.log('✅ Formulaire VALIDE');
      
      if (this.inscriptionForm.value.password !== this.inscriptionForm.value.confirmPassword) {
        this.errorMessage = 'Les mots de passe ne correspondent pas';
        return;
      }
      
      this.isLoading = true;
      this.errorMessage = '';
      this.successMessage = '';
      
      const userData = {
        nom: this.inscriptionForm.value.nom,
        prenom: this.inscriptionForm.value.prenom,
        nomUtilisateur: this.inscriptionForm.value.nomUtilisateur,
        email: this.inscriptionForm.value.email,
        password: this.inscriptionForm.value.password
      };
      
      console.log('📤 DONNÉES ENVOYÉES:', userData);
      
      this.authService.inscrire(userData).subscribe({
        next: (response) => {
          console.log('✅ RÉPONSE REÇUE:', response);
          this.isLoading = false;
          if (response.success) {
            this.email = this.inscriptionForm.value.email;
            this.successMessage = response.message;
            this.step = 'confirmation';
          } else {
            this.errorMessage = response.message || 'Erreur lors de l\'inscription';
          }
        },
        error: (error) => {
          console.error('❌ ERREUR HTTP:', error);
          this.isLoading = false;
          this.errorMessage = 'Erreur: ' + (error.message || 'Vérifie le backend');
        }
      });
    } else {
      console.log('❌ Formulaire INVALIDE');
      this.errorMessage = 'Veuillez remplir tous les champs correctement.';
    }
  }

  goBackToForm(): void {
    this.step = 'form';
    this.errorMessage = '';
    this.successMessage = '';
  }

  confirmer(code: string): void {
    console.log('🔵 Confirmation avec code:', code);
    if (!code || code.length !== 6) {
      this.errorMessage = 'Le code doit contenir 6 chiffres';
      return;
    }
    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';
    this.authService.confirmerCompte(this.email, code).subscribe({
      next: (response) => {
        this.isLoading = false;
        if (response.success) {
          this.successMessage = response.message;
          setTimeout(() => this.router.navigate(['/login']), 3000);
        } else {
          this.errorMessage = response.message || 'Code incorrect';
        }
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage = error.error?.message || 'Code incorrect ou expiré';
      }
    });
  }

  renvoyerCode(): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';
    this.authService.renvoyerCode(this.email).subscribe({
      next: (response) => {
        this.isLoading = false;
        if (response.success) {
          this.successMessage = response.message;
        } else {
          this.errorMessage = response.message || 'Erreur lors du renvoi du code';
        }
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage = error.error?.message || 'Erreur lors du renvoi du code';
      }
    });
  }

  goToLogin(): void {
    this.router.navigate(['/login']);
  }
}
