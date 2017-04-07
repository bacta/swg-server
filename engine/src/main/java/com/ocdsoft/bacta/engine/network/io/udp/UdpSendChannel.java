package com.ocdsoft.bacta.engine.network.io.udp;

import com.ocdsoft.bacta.engine.network.UdpConnection;
import com.ocdsoft.bacta.engine.network.handler.OutgoingMessageHandler;
import com.ocdsoft.bacta.engine.network.io.udp.UdpTransceiver;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.ByteBuffer;

/**
 * Created by kyle on 4/4/2017.
 */
@Singleton
public final class UdpSendChannel {

    private UdpTransceiver transceiver;
    private OutgoingMessageHandler handler;

    @Inject
    public UdpSendChannel() {
        transceiver = null;
        handler = null;
    }

    public boolean available() {
        return transceiver != null && transceiver.isAvailable() && handler != null;
    }

    public void send(UdpConnection connection, ByteBuffer message) {
        message = handler.handleOutgoing(connection, message);
        transceiver.sendMessage(connection, message);
    }

    public void setTransceiver(final UdpTransceiver transceiver) {
        this.transceiver = transceiver;
    }

    public void setHandler(OutgoingMessageHandler handler) {
        this.handler = handler;
    }
}
