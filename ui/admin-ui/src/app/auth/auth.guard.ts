import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

/**
 * The auth guard is used to protect pages with authentication. If a user attempts to access the page
 * without first being authenticated, then they should be redirected to the login page. Please keep in
 * mind that client side authentication cannot completely prevent someone from accessing client side
 * data. It is important to ensure that server side data is not sent to the client without the client
 * first authenticating with the server. Expect users to try and circumvent the client side security, and
 * restrict their access on the server side as well. Think of the client side authentication mechanism as
 * more of a quality of life improvement for the user's experience. It is merely to help the user fascilitate
 * obtaining and transmitting their authentication details with their requests to the server.
 */
@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router) { }

  canActivate(): Observable<boolean> | boolean {
    //TODO: More work needed here.
    return this.authService.isLoggedIn();
  }
}
