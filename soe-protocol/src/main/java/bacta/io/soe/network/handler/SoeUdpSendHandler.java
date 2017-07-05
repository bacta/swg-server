package bacta.io.soe.network.handler;

import bacta.io.network.udp.UdpEmitter;
import bacta.io.soe.network.connection.SoeUdpConnectionCache;

/**
 * Created by kyle on 7/3/2017.
 */
public interface SoeUdpSendHandler {
    void start(final String metricsPrefix, final SoeUdpConnectionCache connectionCache, final UdpEmitter udpEmitter);
}
