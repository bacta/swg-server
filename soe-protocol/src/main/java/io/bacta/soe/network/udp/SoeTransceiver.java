package io.bacta.soe.network.udp;

import io.bacta.engine.network.udp.UdpChannel;
import io.bacta.shared.GameNetworkMessage;
import io.bacta.soe.network.connection.SoeConnection;
import io.bacta.soe.network.connection.SoeConnectionCache;
import io.bacta.soe.network.handler.SoeInboundMessageChannel;
import io.bacta.soe.network.handler.SoeSendHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * Created by kyle on 7/12/2017.
 */

@Slf4j
@Component
@Scope("prototype")
public class SoeTransceiver {

    private UdpChannel udpChannel;
    private final SoeInboundMessageChannel inboundMessageChannel;
    private final SoeSendHandler sendHandler;
    private final SoeConnectionCache soeConnectionCache;

    private String name = "";

    @Inject
    public SoeTransceiver(final UdpChannel udpChannel,
                          final SoeInboundMessageChannel inboundMessageChannel,
                          final SoeSendHandler sendHandler) {

        this.udpChannel = udpChannel;
        this.inboundMessageChannel = inboundMessageChannel;
        this.sendHandler = sendHandler;
        this.soeConnectionCache = inboundMessageChannel.getConnectionCache();
    }

    public void start(final String name) throws UnknownHostException {
        start(name, InetAddress.getByName("0.0.0.0"), 0);
    }

    public void start(final String name, final InetAddress bindAddress, final int bindPort) {
        this.name = name;
        this.udpChannel.start(name, bindAddress, bindPort, inboundMessageChannel);
        this.sendHandler.start(name, soeConnectionCache, inboundMessageChannel.getProtocolHandler(), udpChannel);
    }

    public SoeConnection getConnection(final InetSocketAddress address) {
        SoeConnection connection = soeConnectionCache.get(address);
        if(connection == null) {
            connection = inboundMessageChannel.getConnectionProvider().newOutgoingInstance(address);
            soeConnectionCache.put(address, connection);
        }

        return connection;
    }

    public void broadcast(GameNetworkMessage message) {
        soeConnectionCache.broadcast(message);
    }

    public void stop() throws Exception {
        LOGGER.info("Shutting down SoeTransceiver({})", name);
        udpChannel.stop();
        sendHandler.stop();
    }
}
