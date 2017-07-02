package com.ocdsoft.bacta.soe.config;

import com.ocdsoft.bacta.soe.network.EncryptMethod;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.Collection;
import java.util.Set;

/**
 * Created by kburkhardt on 2/7/15.
 */

@Component
@Data
@ConfigurationProperties(prefix = "soe.network.shared")
public final class SoeNetworkConfigurationImpl implements SoeNetworkConfiguration {

    private InetAddress bindAddress;
    private int bindPort;

    private InetAddress publicAddress;
    private Collection<String> trustedClients;
    private boolean compression;
    private int protocolVersion;
    private EncryptMethod encryptMethod;

    private boolean logAllNetworkTraffic;
    private byte crcBytes;
    private int hashTableSize;
    private int incomingBufferSize;
    private int outgoingBufferSize;
    private int maxConnections;
    private int maxRawPacketSize;
    private int maxInstandingPackets;
    private int maxOutstandingBytes;
    private int maxOutstandingPackets;
    private boolean processOnSend;
    private boolean processIcmpErrors;
    private int fragmentSize;
    private int pooledPacketMax;
    private int pooledPacketSize;
    private int packetHistoryMax;
    private int oldestUnacknowledgedTimeout;
    private int overflowLimit;
    private int reportStatisticsInterval;
    private int packetSizeWarnThreshold;
    private int packetCountWarnThreshold;
    private int byteCountWarnThreshold;
    private boolean reportMessages;
    private int congestionWindowMinimum;
    private int stallReportDelay;
    private boolean enableFlushAndConfirmAllData;
    private boolean fatalOnConnectionClosed;
    private int logBackloggedPacketThreshold;
    private boolean useNetworkThread;
    private int networkThreadSleepTimeMs;
    private int keepAliveDelay;
    private int pooledPacketInitial;
    private int maxDataHoldTime;
    private int resendDelayAdjust;
    private int resendDelayPercent;
    private int networkThreadPriority;
    private int noDataTimeout;
    private int reliableOverflowBytes;
    private int icmpErrorRetryPeriod;
    private int maxDataHoldSize;
    private boolean allowPortRemapping;
    private boolean useTcp;
    private int tcpMinimumFrame;
    private boolean reportUdpDisconnects;
    private boolean reportTcpDisconnects;
    private boolean logConnectionConstructionDestruction;
    private boolean logConnectionOpenedClosed;
    private boolean logConnectionDeferredMessagesWarning;
    private int logConnectionDeferredMessagesWarningInterval;
    private int maxTCPRetries;
    private boolean logSendingTooMuchData;
    private Set<Integer> reservedPorts;
    private boolean networkHandlerDispatchThrottle;
    private int networkHandlerDispatchThrottleTimeMilliseconds;
    private int networkHandlerDispatchQueueSize;

    public final int getMaxReliablePayload() {
        return maxRawPacketSize - crcBytes - 5;
    }

    private boolean multiSoeMessages;
    private boolean multiGameMessages;
    private int connectionsPerAccount;
    private String requiredClientVersion;
//
//    private int encryptCode;
//    private boolean compression;
}
