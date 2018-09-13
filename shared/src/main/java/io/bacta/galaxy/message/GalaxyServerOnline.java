package io.bacta.galaxy.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.net.InetSocketAddress;

@Getter
@AllArgsConstructor
public final class GalaxyServerOnline {
    private final InetSocketAddress address;
}
