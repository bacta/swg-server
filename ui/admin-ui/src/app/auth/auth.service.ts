import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable, of, BehaviorSubject } from 'rxjs';
import { Router } from '@angular/router';
import * as moment from 'moment';
import { User } from '../models/user';
import { shareReplay } from 'rxjs/operators';

export const AUTH_TOKEN_KEY = "token";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  /**
   * The current user should be set on authorize, and unset on logout.
   */
  private authenticatedUser: User;

  //public login$ = new BehaviorSubject<AuthResponse>(null);
  public logout$ = new BehaviorSubject<User>(null);

  constructor(
    private httpClient: HttpClient,
    private router: Router) { }

    get user(): User {
      return this.authenticatedUser;
    }


  getUser(): User {
    return this.authenticatedUser;
  }

  isLoggedIn(): boolean {
    return true;
  }

  getToken(): string {
    return localStorage.getItem(AUTH_TOKEN_KEY);
  }

  /**
   * Logs the user out by removing their authentication token, and any other session state that needs to be invalidated.
   * Will return the user to the login page upon success.
   */
  logout(redirectUrl?: string) {
    localStorage.removeItem(AUTH_TOKEN_KEY);

    const navigationExtras = { queryParams: {} };

    if (redirectUrl)
      navigationExtras.queryParams = {
        redirect: redirectUrl
      }

    this.logout$.next(this.authenticatedUser);
    this.authenticatedUser = null;

    //Redirect to login page, with a redirect url attached as a query param if it's been set.
    this.router.navigate(['/login'], navigationExtras);
  }
}
