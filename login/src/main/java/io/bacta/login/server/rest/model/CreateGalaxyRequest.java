package io.bacta.login.server.rest.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class CreateGalaxyRequest {
    private final String name;
    private final String address;
    private final int port;
    private final int timeZone;
}
