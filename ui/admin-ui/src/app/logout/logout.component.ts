import { AuthService } from "../auth/auth.service";
import { Component } from "@angular/core";

@Component({
    template: ''
})
export class LogoutComponent {
    constructor(authService: AuthService) {
        authService.logout();
    }
}