import { AuthService } from "../auth/auth.service";
import { OnInit, Component } from "@angular/core";

@Component({
    template: ''
})
export class LogoutComponent implements OnInit {
    constructor(private authService: AuthService) { }

    ngOnInit() {
        this.authService.logout();
    }
}