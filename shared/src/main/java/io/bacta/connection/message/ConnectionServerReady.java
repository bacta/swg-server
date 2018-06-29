package io.bacta.connection.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.net.InetSocketAddress;

@Getter
@AllArgsConstructor
public class ConnectionServerReady implements Serializable {
    private final InetSocketAddress address;
}
