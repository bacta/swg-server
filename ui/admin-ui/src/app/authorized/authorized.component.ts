import { Component } from '@angular/core';
import { AuthService } from '../auth/auth.service';

@Component({
  template: ''
})
export class AuthorizedComponent {
  constructor(authService: AuthService) {
    authService.authorized();
  }
}
