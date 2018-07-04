package io.bacta.game.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.net.InetSocketAddress;

@Getter
@AllArgsConstructor
public class ZoneSupervisorReady implements Serializable {
    private final String name;
    private final InetSocketAddress address;
}