import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { MarcheListComponent } from './components/marche-list/marche-list.component';
import { MarcheFormComponent } from './components/marche-form/marche-form.component';
import { InscriptionComponent } from './components/inscription/inscription.component';
import { AuthGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'inscription', component: InscriptionComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard] },
  { path: 'marches', component: MarcheListComponent, canActivate: [AuthGuard] },
  { path: 'marches/nouveau', component: MarcheFormComponent, canActivate: [AuthGuard] },
  { path: 'marches/editer/:id', component: MarcheFormComponent, canActivate: [AuthGuard] },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: '**', redirectTo: '/login' }
];
