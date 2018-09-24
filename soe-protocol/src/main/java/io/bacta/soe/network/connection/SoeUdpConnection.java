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

package io.bacta.soe.network.connection;

import io.bacta.engine.network.connection.ConnectionState;
import io.bacta.engine.network.udp.UdpConnection;
import io.bacta.shared.GameNetworkMessage;
import io.bacta.soe.config.SoeNetworkConfiguration;
import io.bacta.soe.network.message.*;
import io.bacta.soe.util.SoeMessageUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.management.ObjectName;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.Consumer;

/**
 * Represents a Udp endpoint for a SOE connection.  Contains both {@link SoeIncomingMessageProcessor} and
 * {@link SoeOutgoingMessageProcessor}
 *
 * This class supports JMX instrumentation for remote management
 */
@Slf4j
@Getter
public final class SoeUdpConnection implements UdpConnection, SoeUdpConnectionMBean {

    private final ObjectName objectName;

    private final InetSocketAddress remoteAddress;
    private int id;

    private int protocolVersion;
    private byte crcBytes;
    private boolean compression;

    private final SoeIncomingMessageProcessor incomingMessageProcessor;
    private final SoeOutgoingMessageProcessor outgoingMessageProcessor;

    private int maxRawPacketSize;
    private int encryptCode;
    private EncryptMethod encryptMethod1;
    private EncryptMethod encryptMethod2;

    private long lastActivity;
    private long lastRemoteActivity;

    private TerminateReason terminateReason;

    @Setter
    private ConnectionState connectionState;

    private int masterPingTime;
    private int averagePingTime;
    private int lowPingTime;
    private int highPingTime;
    private int lastPingTime;

    private Consumer<SoeUdpConnection> connectCallback;

    @Inject
    public SoeUdpConnection(final ObjectName objectName,
                            final InetSocketAddress sender,
                            final int id,
                            final SoeNetworkConfiguration networkConfiguration,
                            final SoeIncomingMessageProcessor incomingMessageProcessor,
                            final SoeOutgoingMessageProcessor outgoingMessageProcessor) {

        this.objectName = objectName;
        this.remoteAddress = sender;
        this.id = id;
        this.connectionState = ConnectionState.NEW;

        this.protocolVersion = networkConfiguration.getProtocolVersion();
        this.crcBytes = networkConfiguration.getCrcBytes();
        this.compression = networkConfiguration.isCompression();

        this.maxRawPacketSize = networkConfiguration.getMaxRawPacketSize();
        this.terminateReason = TerminateReason.NONE;

        this.incomingMessageProcessor = incomingMessageProcessor;
        this.outgoingMessageProcessor = outgoingMessageProcessor;

        this.encryptMethod1 = EncryptMethod.USERSUPPLIED;
        this.encryptMethod2 = EncryptMethod.XOR;

        masterPingTime = 0;
        averagePingTime = 0;
        lowPingTime = 0;
        highPingTime = 0;
        lastPingTime = 0;

        updateLastActivity();
        updateLastClientActivity();
    }

    /**
     * Update last activity time stamp of when this object initiated and outgoing change
     */
    public void updateLastActivity() {
        lastActivity = System.currentTimeMillis();
    }

    /**
     * Update last activity timestamp of when the remote object has sent a message
     */
    public void updateLastClientActivity() {
        lastRemoteActivity = System.currentTimeMillis();
    }

    public void sendMessage(SoeMessage message) {
        outgoingMessageProcessor.process(message);
    }

    public void sendMessage(GameNetworkMessage message) {
        if(!outgoingMessageProcessor.process(message)) {
            terminate(TerminateReason.RELIABLEOVERFLOW);
        }
    }

    public List<ByteBuffer> getPendingMessages() {

        List<ByteBuffer> messages = outgoingMessageProcessor.getPendingMessages();
        if(!messages.isEmpty()) {
            updateLastActivity();
        }
        return messages;
    }

    public void ackClient(short sequenceNum) {
        updateLastClientActivity();
        outgoingMessageProcessor.handleClientAck(sequenceNum);
        sendMessage(new AckMessage(sequenceNum));
    }

    public void ackAllFromClient(short sequenceNum) {
        updateLastClientActivity();
        outgoingMessageProcessor.handleClientAckAll(sequenceNum);
        sendMessage(new AckAllMessage(sequenceNum));
    }

    public void connect(Consumer<SoeUdpConnection> connectCallback) {
        this.connectCallback = connectCallback;
        ConnectMessage connectMessage = new ConnectMessage(protocolVersion, id, maxRawPacketSize);
        sendMessage(connectMessage);
    }

    public void doConfirm(int connectionId, int encryptCode, int maxRawPacketSize, EncryptMethod encryptMethod1, EncryptMethod encryptMethod2) {

        this.id = connectionId;
        this.encryptCode = encryptCode;
        this.maxRawPacketSize = maxRawPacketSize;
        this.encryptMethod1 = encryptMethod1;
        this.encryptMethod2 = encryptMethod2;

        setConnectionState(ConnectionState.ONLINE);

        ConfirmMessage response = new ConfirmMessage(
                crcBytes,
                connectionId,
                encryptCode,
                encryptMethod1,
                encryptMethod2,
                maxRawPacketSize
        );

        sendMessage(response);
    }

    public void confirmed(int connectionID, int encryptCode, byte crcBytes, EncryptMethod encryptMethod1, EncryptMethod encryptMethod2, int maxRawPacketSize) {
        this.id = connectionID;
        this.encryptCode = encryptCode;
        this.crcBytes = crcBytes;
        this.compression = compression;
        this.maxRawPacketSize = maxRawPacketSize;
        this.encryptMethod1 = encryptMethod1;
        this.encryptMethod2 = encryptMethod2;

        setConnectionState(ConnectionState.ONLINE);

        if(connectCallback != null) {
            connectCallback.accept(this);
        }
    }

    public void terminate(TerminateReason reason) {
        terminate(reason, true);
    }

    public void terminate(TerminateReason reason, boolean sendTerminate) {
        if(sendTerminate) {
            Terminate terminate = new Terminate(this.getId(), reason);
            sendMessage(terminate);
        }
        
        setConnectionState(ConnectionState.DISCONNECTED);
        terminateReason = reason;
    }

    /**
     * Updates ping information sent by remote client and received in {@link io.bacta.soe.network.controller.ClockSyncController}
     * @param masterPingTime
     * @param averagePingTime
     * @param lowPingTime
     * @param highPingTime
     * @param lastPingTime
     */
    public void updatePingData(final int masterPingTime, final int averagePingTime, final int lowPingTime, final int highPingTime, final int lastPingTime) {
        this.masterPingTime = masterPingTime;
        this.averagePingTime = averagePingTime;
        this.lowPingTime = lowPingTime;
        this.highPingTime = highPingTime;
        this.lastPingTime = lastPingTime;
    }

    /**
     * Get number of SOE protocol messages this connection has sent
     * @return number of {@link SoeMessage} packets sent
     */
    public long getProtocolMessagesSent() {
        return outgoingMessageProcessor.getOutgoingProtocolMessageCount();
    }

    /**
     * Get number of SOE protocol message received
     * @return number of {@link SoeMessage} packets received
     */
    public long getProtocolMessagesReceived() {
        return incomingMessageProcessor.getIncomingProtocolMessageCount();
    }

    public void logReceivedMessage(GameNetworkMessage incomingMessage) {
        if(LOGGER.isTraceEnabled()) {
            LOGGER.trace("{} received {} {}", objectName, incomingMessage.getClass().getSimpleName(), SoeMessageUtil.bytesToHex(incomingMessage));
        }
    }


//    013CA650	UdpManager::UdpManager(UdpManager::Params const *)
//    013CAEB0	UdpManager::~UdpManager(void)
//    013CB150	UdpManager::CreateAndBindSocket(int)
//    013CB370	UdpManager::CloseSocket(void)
//    013CB3C0	UdpManager::GetErrorCondition(void)
//    013CB3E0	UdpManager::ProcessDisconnectPending(void)
//    013CB480	UdpManager::RemoveConnection(UdpConnection *)
//    013CB5E0	UdpManager::AddConnection(UdpConnection *)
//    013CB700	UdpManager::FlushAllMultiBuffer(void)
//    013CB760	UdpManager::GiveTime(int,bool)
//    013CBA90	UdpManager::EstablishConnection(char const *,int,int)
//    013CBC40	UdpManager::KeepUntilDisconnected(UdpConnection *)
//    013CBC90	UdpManager::GetStats(UdpManagerStatistics *)
//    013CBD30	UdpManager::ResetStats(void)
//    013CBD80	UdpManager::DumpPacketHistory(char const *)
//    013CBF80	UdpManager::GetLocalIp(void)
//    013CC040	UdpManager::GetLocalPort(void)
//    013CC100	UdpManager::ActualReceive(void)
//    013CC470	UdpManager::ProcessIcmpErrors(void)
//    013CC490	UdpManager::ActualSend(uchar const *,int,UdpIpAddress,int)
//    013CC6A0	UdpManager::ActualSendHelper(uchar const *,int,UdpIpAddress,int)
//    013CC7B0	UdpManager::SendPortAlive(UdpIpAddress,int)
//    013CC880	UdpManager::ProcessRawPacket(UdpManager::PacketHistoryEntry const *)
//    013CCC30	UdpManager::AddressGetConnection(UdpIpAddress,int)
//    013CCD20	UdpManager::ConnectCodeGetConnection(int)
//    013CCDF0	UdpManager::WrappedBorrow(LogicalPacket const *)
//    013CCF00	UdpManager::WrappedCreated(WrappedLogicalPacket *)
//    013CCF60	UdpManager::WrappedDestroyed(WrappedLogicalPacket *)
//    013CCFF0	UdpManager::CreatePacket(void const *,int,void const *,int)
//    013CD160	UdpManager::PoolCreated(PooledLogicalPacket *)
//    013CD1C0	UdpManager::PoolDestroyed(PooledLogicalPacket *)
//    013CD250	UdpManager::PacketHistoryEntry::PacketHistoryEntry(int)
//    013CD2C0	UdpManager::PacketHistoryEntry::~PacketHistoryEntry(void)

//    013CD300	UdpConnection::UdpConnection(UdpManager *,UdpIpAddress,int,int)
//    013CD390	UdpConnection::UdpConnection(UdpManager *,UdpConnection::PacketHistoryEntry const *)
//    013CD4B0	UdpConnection::INIT(UdpManager *,UdpIpAddress,int)
//    013CD770	UdpConnection::~UdpConnection(void)
//    013CD840	UdpConnection::PortUnreachable(void)
//    013CD900	UdpConnection::InternalDisconnect(int,UdpConnection::DisconnectReason)
//    013CDA70	UdpConnection::SendTerminatePacket(int,UdpConnection::DisconnectReason)
//    013CDB30	UdpConnection::SetSilentDisconnect(bool)
//    013CDB50	UdpConnection::Send(UdpChannel,void const *,int)
//    013CDCE0	UdpConnection::Send(UdpChannel,LogicalPacket const *)
//    013CDEA0	UdpConnection::InternalSend(UdpChannel,uchar const *,int,uchar const *,int)
//    013CE2C0	UdpConnection::InternalSend(UdpChannel,LogicalPacket const *)
//    013CE420	UdpConnection::PingStatReset(void)
//    013CE4F0	UdpConnection::GetStats(UdpConnectionStatistics *)
//    013CE660	UdpConnection::ProcessRawPacket(UdpManager::PacketHistoryEntry const *)
//    013CEC60	UdpConnection::CallbackRoutePacket(uchar const *,int)
//    013CED20	UdpConnection::CallbackCorruptPacket(uchar const *,int,UdpCorruptionReason)
//    013CEDD0	UdpConnection::ProcessCookedPacket(uchar const *,int)
//    013CFE30	UdpConnection::FlushChannels(void)
//    013CFE70	UdpConnection::FlagPortUnreachable(void)
//    013CFE90	UdpConnection::GiveTime(void)
//    013CFF00	UdpConnection::InternalGiveTime(void)
//    013D0840	UdpConnection::TotalPendingBytes(void)
//    013D08C0	UdpConnection::RawSend(uchar const *,int)
//    013D09D0	UdpConnection::ExpireSendBin(void)
//    013D0AC0	UdpConnection::ExpireReceiveBin(void)
//    013D0BB0	UdpConnection::PhysicalSend(uchar const *,int,bool)
//    013D1020	UdpConnection::BufferedSend(uchar const *,int,uchar const *,int,bool)
//    013D1290	UdpConnection::InternalAckSend(uchar *,uchar const *,int)
//    013D12F0	UdpConnection::FlushMultiBuffer(void)
//    013D13E0	UdpConnection::EncryptNone(uchar *,uchar const *,int)
//    013D1420	UdpConnection::DecryptNone(uchar *,uchar const *,int)
//    013D1460	UdpConnection::EncryptUserSupplied(uchar *,uchar const *,int)
//    013D14F0	UdpConnection::DecryptUserSupplied(uchar *,uchar const *,int)
//    013D1580	UdpConnection::EncryptUserSupplied2(uchar *,uchar const *,int)
//    013D1610	UdpConnection::DecryptUserSupplied2(uchar *,uchar const *,int)
//    013D16A0	UdpConnection::EncryptXorBuffer(uchar *,uchar const *,int)
//    013D1770	UdpConnection::DecryptXorBuffer(uchar *,uchar const *,int)
//    013D1850	UdpConnection::EncryptXor(uchar *,uchar const *,int)
//    013D1900	UdpConnection::DecryptXor(uchar *,uchar const *,int)
//    013D19B0	UdpConnection::SetupEncryptModel(void)
//    013D1D40	UdpConnection::GetChannelStatus(UdpChannel,UdpConnection::ChannelStatus *)
//    013D1DC0	UdpConnection::DisconnectReasonText(UdpConnection::DisconnectReason)
}
