package com.ocdsoft.bacta.soe.config;

import com.ocdsoft.bacta.engine.conf.NetworkConfiguration;
import com.ocdsoft.bacta.soe.network.EncryptMethod;

import java.net.InetAddress;
import java.util.Collection;
import java.util.Set;

/**
 * Created by kyle on 6/28/2017.
 */
public interface SoeNetworkConfiguration extends NetworkConfiguration {

    InetAddress getPublicAddress();
    Collection<String> getTrustedClients();
    boolean isCompression();
    int getProtocolVersion();
    EncryptMethod getEncryptMethod();
    int getMaxReliablePayload();

    int getMaxRawPacketSize();
    int getNetworkThreadSleepTimeMs();
    boolean isReportUdpDisconnects();
    int getResendDelayAdjust();
    int getResendDelayPercent();
    int getNoDataTimeout();
    int getMaxInstandingPackets();
    int getMaxOutstandingPackets();
    boolean isLogAllNetworkTraffic();
    byte getCrcBytes();
    int getHashTableSize();
    int getIncomingBufferSize();
    int getOutgoingBufferSize();
    int getMaxConnections();
    int getMaxOutstandingBytes();
    boolean isProcessOnSend();
    boolean isProcessIcmpErrors();
    int getFragmentSize();
    int getPooledPacketMax();
    int getPooledPacketSize();
    int getPacketHistoryMax();
    int getOldestUnacknowledgedTimeout();
    int getOverflowLimit();
    int getReportStatisticsInterval();
    int getPacketSizeWarnThreshold();
    int getPacketCountWarnThreshold();
    int getByteCountWarnThreshold();
    boolean isReportMessages();
    int getCongestionWindowMinimum();
    int getStallReportDelay();
    boolean isEnableFlushAndConfirmAllData();
    boolean isFatalOnConnectionClosed();
    int getLogBackloggedPacketThreshold();
    boolean isUseNetworkThread();
    int getKeepAliveDelay();
    int getPooledPacketInitial();
    int getMaxDataHoldTime();
    int getNetworkThreadPriority();
    int getReliableOverflowBytes();
    int getIcmpErrorRetryPeriod();
    int getMaxDataHoldSize();
    boolean isAllowPortRemapping();
    boolean isUseTcp();
    int getTcpMinimumFrame();
    boolean isReportTcpDisconnects();
    boolean isLogConnectionConstructionDestruction();
    boolean isLogConnectionOpenedClosed();
    boolean isLogConnectionDeferredMessagesWarning();
    int getLogConnectionDeferredMessagesWarningInterval();
    int getMaxTCPRetries();
    boolean isLogSendingTooMuchData();
    Set<Integer> getReservedPorts();
    boolean isNetworkHandlerDispatchThrottle();
    int getNetworkHandlerDispatchThrottleTimeMilliseconds();
    int getNetworkHandlerDispatchQueueSize();

    boolean isMultiGameMessages();
    boolean isMultiSoeMessages();
    int getConnectionsPerAccount();
    String getRequiredClientVersion();
}
