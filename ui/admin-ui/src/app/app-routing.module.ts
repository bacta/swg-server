import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router'
import { BactaAccountsComponent } from './bacta-accounts/bacta-accounts.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { BactaAccountComponent } from './bacta-account/bacta-account.component';
import { GalaxiesComponent } from './galaxies/galaxies.component';
import { AuthGuard } from './auth/auth.guard';
import { AuthorizedComponent } from './authorized/authorized.component';
import { LogoutComponent } from './logout/logout.component';

const routes: Routes = [
  { path: 'accounts', component: BactaAccountsComponent, canActivate: [AuthGuard] },
  { path: 'accounts/:id', component: BactaAccountComponent, canActivate: [AuthGuard] },
  { path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard] },
  { path: 'galaxies', component: GalaxiesComponent, canActivate: [AuthGuard] },
  { path: 'authorized', component: AuthorizedComponent },
  { path: 'logout', component: LogoutComponent },

  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  { path: '**', redirectTo: '/dashboard' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
