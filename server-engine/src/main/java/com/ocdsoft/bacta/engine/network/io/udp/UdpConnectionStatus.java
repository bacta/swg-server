package com.ocdsoft.bacta.engine.network.io.udp;

import lombok.Getter;

/**
 * Created by Kyle on 8/21/2014.
 */
public enum UdpConnectionStatus {
    NEGOTIATING(0x0),
    CONNECTED(0x1),
    DISCONNECTED(0x2),
    DISCONNECTPENDING(0x3);

    @Getter
    int value;

    UdpConnectionStatus(int value) {
        this.value = value;
    }
}
