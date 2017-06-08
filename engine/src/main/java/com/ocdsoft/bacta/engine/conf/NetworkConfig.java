package com.ocdsoft.bacta.engine.conf;

import java.net.InetAddress;
import java.util.Collection;

/**
 * Created by kyle on 4/14/2017.
 */
public interface NetworkConfig {
    InetAddress getBindAddress();
    int getBindPort();
    InetAddress getPublicAddress();
    Collection<String> getTrustedClients();
    int getMaxRawPacketSize();
    boolean isCompression();
    int getNetworkThreadSleepTimeMs();
    boolean isReportUdpDisconnects();
    int getResendDelayAdjust();
    int getResendDelayPercent();
    int getNoDataTimeout();
    int getMaxInstandingPackets();
    int getMaxOutstandingPackets();
    boolean isDisableInstrumentation();
    boolean isLogAllNetworkTraffic();
}
