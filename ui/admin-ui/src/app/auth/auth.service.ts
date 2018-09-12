import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { BehaviorSubject } from 'rxjs';
import { Router } from '@angular/router';
import { User } from '../models/user';
import { UrlHelper } from '../helpers/url-helper';
import * as QueryString from 'query-string';

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
    const token = this.getToken();

    //TODO: Check if its expired.

    return !!token;
  }

  getToken(): string {
    return localStorage.getItem(AUTH_TOKEN_KEY);
  }

  authorized() {
    const hash = window.location.hash.substr(1);
    const hashed = QueryString.parse(hash);

    //extract the referrer if it exists.
    const search = QueryString.parse(window.location.search);
    let referrer = '/'; //Default to dashboard.

    //If a referrer was passed, then redirect them to that instead.
    if (search.ref)
      referrer = search.ref;

    //We should verify the jwt here.
    //this.verifyJwt(hashed.access_token);

    localStorage.setItem('token', JSON.stringify(hashed));

    //Redirect them to the referrer.
    this.router.navigate([referrer]);
  }

  authorize(referrer?: string) {
    const clientId = environment.auth.clientId;
    const authEndpoint = UrlHelper.build('/authorize', environment.auth.url);
    const responseType = "id_token token";
    const scope = "all";

    let redirectUri = UrlHelper.build('/authorized', window.location.origin);

    if (referrer)
      redirectUri = `${redirectUri}?ref=${encodeURIComponent(referrer)}`;

    const url =
      authEndpoint + "?" +
      "response_type=" + encodeURIComponent(responseType) + "&" +
      "client_id=" + encodeURIComponent(clientId) + "&" +
      "redirect_uri=" + encodeURIComponent(redirectUri) + "&" +
      "scope=" + encodeURIComponent(scope);

    //Redirect the user to the oauth server.
    window.location.href = url;
  }

  /**
   * Logs the user out by removing their authentication token, and any other session state that needs to be invalidated.
   * Will return the user to the login page upon success.
   */
  logout(referrer?: string) {
    localStorage.removeItem(AUTH_TOKEN_KEY);

    this.logout$.next(this.authenticatedUser);
    this.authenticatedUser = null;

    this.authorize(referrer);
  }
}
