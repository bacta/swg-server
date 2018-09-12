import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { JwtModule } from '@auth0/angular-jwt';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { AppComponent } from './app.component';
import { BactaAccountComponent } from './bacta-account/bacta-account.component';
import { BactaAccountsComponent } from './bacta-accounts/bacta-accounts.component';
import { AppRoutingModule } from './app-routing.module';
import { DashboardComponent } from './dashboard/dashboard.component';
import { GalaxiesComponent } from './galaxies/galaxies.component';
import { AutofocusDirective } from './directives/autofocus.directive';
import { AuthorizedComponent } from './authorized/authorized.component';
import { LogoutComponent } from './logout/logout.component';
import { AuthInterceptor } from './auth/auth.interceptor';

export function tokenGetter() {
  return localStorage.getItem('access_token');
}

@NgModule({
  declarations: [
    AppComponent,
    BactaAccountComponent,
    BactaAccountsComponent,
    DashboardComponent,
    GalaxiesComponent,
    AutofocusDirective,
    AuthorizedComponent,
    LogoutComponent
  ],
  entryComponents: [
  ],
  imports: [
    NgbModule.forRoot(),
    BrowserModule,
    FormsModule,
    AppRoutingModule,
    HttpClientModule,
    JwtModule.forRoot({
      config: {
        tokenGetter: tokenGetter,
        whitelistedDomains: ['localhost:8080', 'bacta.io'],
        blacklistedRoutes: ['localhost:8080/oauth/token', 'bacta.io/oauth/token', 'bacta.io/oauth/authorize']
      }
    })
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }
    //{ provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true },
    //{ provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
