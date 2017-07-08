/*
 * Copyright 2017. Bacta
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.bacta.soe.config;

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
@ConfigurationProperties(prefix = "bacta.network.shared")
public final class SoeNetworkConfigurationImpl implements SoeNetworkConfiguration {

    private InetAddress bindAddress;
    private int bindPort;

    private InetAddress publicAddress;
    private Collection<String> trustedClients;
    private boolean compression;
    private int protocolVersion;

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
    private String metricsPrefix;
    private String baseMessageClassPath;
//
//    private int encryptCode;
//    private boolean compression;
}
