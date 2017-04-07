package com.ocdsoft.bacta.soe.protocol.network.io.udp;

import com.ocdsoft.bacta.engine.conf.BactaConfiguration;
import com.ocdsoft.bacta.soe.protocol.network.connection.EncryptMethod;
import lombok.Getter;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Created by kyle on 4/12/2016.
 */
@Getter
public abstract class BaseNetworkConfiguration implements NetworkConfiguration {

    protected final InetAddress bindAddress;
    protected final int udpPort;
    private final int pingPort;
    protected final InetAddress publicAddress;
    protected Collection<String> trustedClients;
    protected final String basePackage;

    private final int protocolVersion;
    private final int maxRawPacketSize;
    private final byte crcBytes;
    private final EncryptMethod encryptMethod;

    private final boolean compression;
    private final int networkThreadSleepTimeMs;

    private final boolean reportUdpDisconnects;

    private final int resendDelayAdjust;
    private final int resendDelayPercent;

    private final int noDataTimeout;
    private final int maxInstandingPackets;
    private final int maxOutstandingPackets;

    private final boolean multiSoeMessages;
    private final boolean multiGameMessages;
    private final int connectionsPerAccount;

    private final boolean disableInstrumentation;
    private final String requiredClientVersion;

    //    logAllNetworkTraffic = false
    //    incomingBufferSize = 4194304
    //    outgoingBufferSize = 4194304
    //    maxConnections = 1000
    //
    //    maxOutstandingBytes = 204800
    //    fragmentSize = 496
    //    pooledPacketMax = 1024
    //    packettHistoryMax = 100
    //    oldestUnacknowledgedTimeout = 90000
    //    reportStatisticsInterval = 60000
    //
    //    pooledPAcketInitial = 1024
    //
    //
    //    reliableOverflowBytes = 2097152
    //    logConnectionConstructionDestruction = false
    //    logConnectionOpenedClosed = false

    protected BaseNetworkConfiguration(final BactaConfiguration configuration, final String serverSection) throws UnknownHostException {

        String bindAddressString= configuration.getString(serverSection, "BindAddress");
        if(bindAddressString.equalsIgnoreCase("localhost")) {
            bindAddress = InetAddress.getLocalHost();
        } else {
            bindAddress = InetAddress.getByName(bindAddressString);
        }
        udpPort = configuration.getInt(serverSection, "UdpPort");
        pingPort = configuration.getInt("Bacta/GameServer", "PingPort");

        String publicAddressString= configuration.getString(serverSection, "PublicAddress");
        if(publicAddressString.equalsIgnoreCase("localhost")) {
            publicAddress = InetAddress.getLocalHost();
        } else {
            publicAddress = InetAddress.getByName(publicAddressString);
        }

        trustedClients = configuration.getStringCollection(serverSection, "TrustedClient");
        LinkedList<String> clients = new LinkedList<>();
        clients.add(bindAddress.getHostAddress());
        clients.add(publicAddress.getHostAddress());
        if(trustedClients != null) {
            clients.addAll(trustedClients);
        }
        trustedClients = Collections.unmodifiableList(clients);

        protocolVersion = configuration.getIntWithDefault("SharedNetwork", "protocolVersion", 2);
        maxRawPacketSize = configuration.getIntWithDefault("SharedNetwork", "maxRawPacketSize", 496);
        crcBytes = configuration.getByteWithDefault("SharedNetwork", "crcBytes", (byte) 2);
        compression = configuration.getBooleanWithDefault("SharedNetwork", "compression", true);
        networkThreadSleepTimeMs = configuration.getIntWithDefault("SharedNetwork", "networkThreadSleepTimeMs", 20);
        reportUdpDisconnects = configuration.getBooleanWithDefault("SharedNetwork", "reportUdpDisconnects", false);
        String method = configuration.getStringWithDefault("SharedNetwork", "encryptMethod", "XOR");
        encryptMethod = EncryptMethod.valueOf(method != null ? method : "NONE");
        resendDelayAdjust = configuration.getIntWithDefault("SharedNetwork", "resendDelayAdjust", 500);
        resendDelayPercent = configuration.getIntWithDefault("SharedNetwork", "resendDelayPercent", 125);
        noDataTimeout = configuration.getIntWithDefault("SharedNetwork", "noDataTimeout", 46000);
        maxInstandingPackets = configuration.getIntWithDefault("SharedNetwork", "maxInstandingPackets", 400);
        maxOutstandingPackets = configuration.getIntWithDefault("SharedNetwork", "maxOutstandingPackets", 400);
        multiSoeMessages = configuration.getBooleanWithDefault("SharedNetwork", "multiSoeMessages", true);
        multiGameMessages = configuration.getBooleanWithDefault("SharedNetwork", "multiGameMessages", true);
        connectionsPerAccount = configuration.getIntWithDefault("SharedNetwork", "connectionsPerAccount", -1);
        disableInstrumentation = configuration.getBooleanWithDefault("SharedNetwork", "disableInstrumentation", false);
        basePackage = configuration.getStringWithDefault("Bacta/Packages", "basePackage", "");
        requiredClientVersion = configuration.getString("SharedNetwork", "requiredClientVersion");
    }

    @Override
    public final int getMaxMultiPayload() {
        return maxRawPacketSize - crcBytes - 5;
    }

    @Override
    public final int getMaxReliablePayload() {
        return maxRawPacketSize - crcBytes - 5;
    }
}
