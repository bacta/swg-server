package io.bacta.login.server.auth.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationRequest {
    private String username;
    private String password;
    private String confirmPassword;
    private String email;
}
