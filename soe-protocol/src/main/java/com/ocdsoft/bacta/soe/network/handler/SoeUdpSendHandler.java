package com.ocdsoft.bacta.soe.network.handler;

import com.ocdsoft.bacta.engine.network.udp.UdpEmitter;
import com.ocdsoft.bacta.soe.network.connection.SoeUdpConnectionCache;

/**
 * Created by kyle on 7/3/2017.
 */
public interface SoeUdpSendHandler {
    void start(final String metricsPrefix, final SoeUdpConnectionCache connectionCache, final UdpEmitter udpEmitter);
}
