package io.bacta.soe.network.udp;

import io.bacta.engine.network.udp.UdpChannel;
import io.bacta.soe.network.handler.SoeUdpSendHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by kyle on 7/12/2017.
 */

@Slf4j
@Service
public class SoeUdpTransceiverGroup {

    private final Set<UdpChannel> udpChannels;
    private final Set<SoeUdpSendHandler> sendHandlers;

    @Inject
    public SoeUdpTransceiverGroup() {
        this.udpChannels = new HashSet<>();
        this.sendHandlers = new HashSet<>();
    }

    public void registerChannel(final UdpChannel channel) {
        this.udpChannels.add(channel);
    }

    public void registerSendHandler(final SoeUdpSendHandler sendHandler) {
        this.sendHandlers.add(sendHandler);
    }

    @PreDestroy
    public void destroy() throws Exception {
        LOGGER.info("Shutting down transceiver group");
        udpChannels.forEach(UdpChannel::destroy);
        sendHandlers.forEach(SoeUdpSendHandler::stop);
    }
}
