import { AuthService } from "./auth.service";
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { of, Observable, throwError } from "rxjs";
import { Router } from "@angular/router";
import { catchError } from "rxjs/operators";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
    constructor(
        private router: Router,
        private authService: AuthService) { }

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        let token = this.authService.getToken();

        if (token) {
            request = request.clone({
                setHeaders: {
                    Authorization: `Bearer ${token}`
                }
            })
        }

        return next.handle(request).pipe(
            catchError(err => this.handleAuthError(err))
        );
    }

    private handleAuthError(err: HttpErrorResponse): Observable<any> {
        //Catch any 401 unauthorized response, log them out of the app, and send them to the login page.
        if (err.status === 401) {
            this.authService.logout(this.router.url);
            return of(err.message)
        }

        //Let all other errors be handled in the pipeline.
        return throwError(err)
    }
}