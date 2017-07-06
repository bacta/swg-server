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

import io.bacta.conf.NetworkConfiguration;
import io.bacta.soe.network.message.EncryptMethod;

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
    String getMetricsPrefix();

    String getBaseMessageClassPath();
}
