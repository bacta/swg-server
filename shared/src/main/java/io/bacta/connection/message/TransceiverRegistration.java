package io.bacta.connection.message;

import java.io.Serializable;
import java.net.InetSocketAddress;

public class TransceiverRegistration implements Serializable {

    private final InetSocketAddress address;

    public TransceiverRegistration(InetSocketAddress address) {
        this.address = address;
    }
}
