import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { AppComponent } from './app.component';
import { LoginComponent } from './components/login/login.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { MarcheListComponent } from './components/marche-list/marche-list.component';
import { MarcheFormComponent } from './components/marche-form/marche-form.component';
import { InscriptionComponent } from './components/inscription/inscription.component';
import { RechercheMarcheComponent } from './components/recherche-marche/recherche-marche.component';
import { AuthInterceptor } from './interceptors/auth.interceptor';
import { FileSizePipe } from './pipes/file-size.pipe';

import { routes } from './app.routes';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    DashboardComponent,
    MarcheListComponent,
    MarcheFormComponent,
    InscriptionComponent,
    RechercheMarcheComponent,
    FileSizePipe
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    ReactiveFormsModule,
    FormsModule,
    RouterModule.forRoot(routes)
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
