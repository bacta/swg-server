package com.ocdsoft.bacta.soe.protocol.io.udp;

/**
 * Created by kyle on 5/28/2016.
 */
public class NoAccountConnectionsAllowedException extends RuntimeException {
    public NoAccountConnectionsAllowedException() {
        super("Server does not accept new connections because 'connectionsPerAccount` is set to 0");
    }

}
