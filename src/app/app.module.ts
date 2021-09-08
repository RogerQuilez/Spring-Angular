import { NgModule, LOCALE_ID } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { registerLocaleData } from '@angular/common';
import localeES from '@angular/common/locales/es';
import { AppComponent } from './app.component';
import { HeaderComponent } from './header/header.component';
import { FooterComponent } from './footer/footer.component';
import { DirectivaComponent } from './directiva/directiva.component';
import { ClientesComponent } from './clientes/clientes.component';
import { PaginatorComponent } from './paginator/paginator.component';

import { ClienteService } from './clientes/cliente.service';

import { RouterModule, Routes } from '@angular/router'; //UrlS de cada componente de nuestra aplicación
import { HttpClientModule } from '@angular/common/http';
import { FormComponent } from './clientes/form.component';
import { FormsModule } from '@angular/forms';
import { DetalleComponent } from './clientes/detalle/detalle.component'; //Modulo para trabajar con formularios


const routes: Routes = [ //De esta manera indicamos una ruta para el componente que deseamos renderizar
  { path: '', redirectTo: '/clientes', pathMatch: 'full' },
  { path: 'directivas', component: DirectivaComponent },
  { path: 'clientes', component: ClientesComponent },
  { path: 'clientes/page/:page', component: ClientesComponent },
  { path: 'clientes/form', component: FormComponent},
  { path: 'clientes/form/:id', component: FormComponent}
]

registerLocaleData(localeES, 'es');

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,
    DirectivaComponent,
    ClientesComponent,
    FormComponent,
    PaginatorComponent,
    DetalleComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    RouterModule.forRoot(routes)
  ],
  providers: [ClienteService, {provide: LOCALE_ID, useValue: 'es' }], //Servicios LOCALE_ID sirve para cambiar el idioma local
  bootstrap: [AppComponent]
})
export class AppModule { }
