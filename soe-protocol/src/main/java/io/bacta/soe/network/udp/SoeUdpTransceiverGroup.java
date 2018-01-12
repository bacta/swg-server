package io.bacta.soe.network.udp;

import io.bacta.engine.network.udp.UdpChannel;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by kyle on 7/12/2017.
 */

@Service
public class SoeUdpTransceiverGroup implements DisposableBean {

    private final Set<UdpChannel> udpChannels;

    @Inject
    public SoeUdpTransceiverGroup() {
        this.udpChannels = new HashSet<>();
    }

    public void registerChannel(UdpChannel channel) {
        this.udpChannels.add(channel);
    }

    @Override
    public void destroy() throws Exception {
        udpChannels.forEach(UdpChannel::destroy);
    }
}
