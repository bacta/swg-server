package io.bacta.engine.network.udp;

/**
 * Created by kyle on 7/12/2017.
 */
public interface UdpTransceiver {
    UdpReceiver getReceiver();
    UdpEmitter getEmitter();
    boolean isReady();
    void stop() throws Exception;
}
