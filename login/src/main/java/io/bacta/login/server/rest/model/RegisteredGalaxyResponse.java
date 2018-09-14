package io.bacta.login.server.rest.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class RegisteredGalaxyResponse {
    private final String name;
    private final String publicKey;
}
