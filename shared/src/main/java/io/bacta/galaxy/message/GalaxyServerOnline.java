package io.bacta.galaxy.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.net.InetSocketAddress;

@Getter
@AllArgsConstructor
public class GalaxyServerOnline implements Serializable {
    private final InetSocketAddress address;
}
