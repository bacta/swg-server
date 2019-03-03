package io.bacta.soe.network.connection;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.net.InetSocketAddress;

@Getter
@AllArgsConstructor
public class ConfigureConnection implements Serializable {
    private final InetSocketAddress remoteAddress;
}
