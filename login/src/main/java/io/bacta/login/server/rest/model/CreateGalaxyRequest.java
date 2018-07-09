package io.bacta.login.server.rest.model;

import lombok.Getter;

@Getter
public final class CreateGalaxyRequest {
    private String name;
    private String address;
    private int port;
    private int timeZone;
}
