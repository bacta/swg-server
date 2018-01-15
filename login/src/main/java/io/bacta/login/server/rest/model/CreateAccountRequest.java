package io.bacta.login.server.rest.model;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class CreateAccountRequest {
    private final String username;
    private final String password;
}
