import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map } from 'rxjs/operators';
import { Router, ActivatedRoute } from '@angular/router';
import * as QueryString from 'query-string';

@Injectable({ providedIn: 'root' })
export class AuthenticationService {
    constructor(private http: HttpClient,
        private router: Router) { }

    authorized() {
        let hash = window.location.hash.substr(1);
        let hashed = QueryString.parse(hash);

        //We should verify the jwt here.
        this.verifyJwt(hashed.access_token);

        localStorage.setItem('token', JSON.stringify(hashed));
    }

    authorize() {
        let authorization_url = "http://localhost:8080/oauth/authorize";
        let client_id = "rest";
        let redirect_uri = "http://localhost:4200/authorized";
        let response_type = "id_token token";
        let scope = "all";

        var url =
            authorization_url + "?" +
            "response_type=" + encodeURI(response_type) + "&" +
            "client_id=" + encodeURI(client_id) + "&" +
            "redirect_uri=" + encodeURI(redirect_uri) + "&" +
            "scope=" + encodeURI(scope);

        window.location.href = url;
    }

    logout() {
        localStorage.removeItem('token');
    }

    private verifyJwt(jwt) {
    }
}