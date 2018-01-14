package io.bacta.soe.network.udp;

import io.bacta.engine.network.udp.UdpChannel;
import io.bacta.soe.network.connection.SoeConnection;
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

    private String name = "";

    @Inject
    public SoeTransceiver(final UdpChannel udpChannel,
                          final SoeInboundMessageChannel inboundMessageChannel,
                          final SoeSendHandler sendHandler) {

        this.udpChannel = udpChannel;
        this.inboundMessageChannel = inboundMessageChannel;
        this.sendHandler = sendHandler;
    }

    public void start(final String name) throws UnknownHostException {
        start(name, InetAddress.getByName("0.0.0.0"), 0);
    }

    public void start(final String name, final InetAddress bindAddress, final int bindPort) {
        this.name = name;
        this.udpChannel.start(name, bindAddress, bindPort, inboundMessageChannel);
        this.sendHandler.start(name, inboundMessageChannel.getConnectionCache(), inboundMessageChannel.getProtocolHandler(), udpChannel);
    }

    public SoeConnection getConnection(final InetSocketAddress address) {
        SoeConnection connection = inboundMessageChannel.getConnectionCache().get(address);
        if(connection == null) {
            connection = inboundMessageChannel.getConnectionProvider().newInstance(address);
            inboundMessageChannel.getConnectionCache().put(address, connection);
        }

        return connection;
    }

    public void stop() throws Exception {
        LOGGER.info("Shutting down SoeTransceiver({})", name);
        udpChannel.stop();
        sendHandler.stop();
    }
}
