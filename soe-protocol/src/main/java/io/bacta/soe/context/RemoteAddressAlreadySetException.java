package io.bacta.soe.context;

import java.net.InetSocketAddress;

public class RemoteAddressAlreadySetException extends RuntimeException {
    public RemoteAddressAlreadySetException(InetSocketAddress remoteAddress) {
        super(remoteAddress.toString());
    }
}
