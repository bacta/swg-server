package com.ocdsoft.bacta.engine.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.util.Collection;

/**
 * Created by kyle on 4/12/2016.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "bacta.network")
public class NetworkConfigImpl implements NetworkConfig {

    private InetAddress bindAddress;
    private int bindPort;
    private InetAddress publicAddress;

    private Collection<String> trustedClients;

    private int maxRawPacketSize;

    private boolean compression;
    private int networkThreadSleepTimeMs;

    private boolean reportUdpDisconnects;

    private int resendDelayAdjust;
    private int resendDelayPercent;

    private int noDataTimeout;
    private int maxInstandingPackets;
    private int maxOutstandingPackets;

    private boolean disableInstrumentation;
    private boolean logAllNetworkTraffic;

}
