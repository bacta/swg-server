package io.bacta.login.server.rest.model;

import io.bacta.login.server.model.GalaxyPopulationStatus;
import io.bacta.login.server.model.GalaxyStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public final class GalaxyListEntry {
    private final String name;
    private final String address;
    private final int port;
    private final int timeZone;

    private GalaxyStatus status;
    private GalaxyPopulationStatus population;

    //Some useful metrics.
    private int onlinePlayers;
    private int maxOnlinePlayers;
    private int uptime;

}
