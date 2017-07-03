package com.ocdsoft.bacta.soe.network.connection;

import com.ocdsoft.bacta.engine.network.ConnectionState;
import com.ocdsoft.bacta.engine.network.udp.UdpConnection;
import com.ocdsoft.bacta.soe.config.SoeUdpConfiguration;
import com.ocdsoft.bacta.soe.network.message.*;
import com.ocdsoft.bacta.soe.config.SoeNetworkConfiguration;
import com.ocdsoft.bacta.soe.serialize.GameNetworkMessageSerializer;
import com.ocdsoft.bacta.soe.util.SoeMessageUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Slf4j
public final class SoeUdpConnection extends UdpConnection {

    @Getter
    private int id;
    
    @Getter
    @Setter
    private SoeUdpConfiguration configuration;

    @Getter
    @Setter
    private int bactaId;

    @Getter
    @Setter
    private String accountUsername;
    
    private ConnectionState state;

    private final UdpMessageProcessor<ByteBuffer> udpMessageProcessor;

    private final AtomicInteger clientSequenceNumber;

    private final IncomingFragmentContainer incomingFragmentContainer;

    private final List<ConnectionRole> roles;

    @Getter
    private long lastActivity;

    @Getter
    private long lastClientActivity;

    private final Consumer<SoeUdpConnection> connectCallback;
    
    @Getter
    private final AtomicInteger gameNetworkMessagesSent;

    @Getter
    private final AtomicInteger protocolMessagesSent;

    @Getter
    private final AtomicInteger protocolMessagesReceived;

    @Getter
    private final AtomicInteger gameNetworkMessagesReceived;

    @Getter
    private TerminateReason terminateReason = TerminateReason.NONE;

    @Getter
    private ObjectName beanName;

    //TODO: Make setters only work once?
    @Getter
    @Setter
    private long currentNetworkId;

    @Getter
    @Setter
    private String currentCharName;

    @Getter
    @Setter
    private int orderedStampLast;

    @Getter
    private int masterPingTime;
    @Getter
    private int averagePingTime;
    @Getter
    private int lowPingTime;
    @Getter
    private int highPingTime;
    @Getter
    private int lastPingTime;

    private final GameNetworkMessageSerializer messageSerializer;

    public SoeUdpConnection(final SoeNetworkConfiguration networkConfiguration,
                            final InetSocketAddress remoteAddress,
                            final GameNetworkMessageSerializer messageSerializer,
                            final Consumer<SoeUdpConnection> connectCallback) {
        
        this.remoteAddress = remoteAddress;
        this.connectCallback = connectCallback;
        this.state = ConnectionState.DISCONNECTED;
        this.messageSerializer = messageSerializer;

        this.configuration = new SoeUdpConfiguration(
                networkConfiguration.getProtocolVersion(),
                networkConfiguration.getCrcBytes(),
                networkConfiguration.getEncryptMethod(),
                networkConfiguration.getMaxRawPacketSize(),
                networkConfiguration.isCompression()
        );

        udpMessageProcessor = new SoeUdpMessageProcessor(this, networkConfiguration);
        clientSequenceNumber = new AtomicInteger();
        incomingFragmentContainer = new IncomingFragmentContainer();
        roles = new ArrayList<>();
        roles.add(ConnectionRole.UNAUTHENTICATED);
        gameNetworkMessagesSent = new AtomicInteger();
        protocolMessagesSent = new AtomicInteger();
        bactaId = -1;

        protocolMessagesReceived = new AtomicInteger();
        gameNetworkMessagesReceived = new AtomicInteger();
        this.orderedStampLast = 0;

        masterPingTime = 0;
        averagePingTime = 0;
        lowPingTime = 0;
        highPingTime = 0;
        lastPingTime = 0;

        updateLastActivity();
        updateLastClientActivity();
    }
    
    public void setId(int id) {
        this.id = id;
        try {
            this.beanName = new ObjectName("Bacta:type=SoeUdpConnection,id=" + id);
        } catch (MalformedObjectNameException e) {
            LOGGER.error("Unable to create bean name", e);
            this.beanName = null;
        }
    }
    
    public void increaseProtocolMessageReceived() {
        protocolMessagesReceived.incrementAndGet();
    }

    public void increaseGameNetworkMessageReceived() {
        gameNetworkMessagesReceived.incrementAndGet();
    }

    public void sendMessage(SoeMessage message) {

        if(state != ConnectionState.DISCONNECTED) {

            protocolMessagesSent.getAndIncrement();

            if (udpMessageProcessor.addUnreliable(message.slice())) {
                updateLastActivity();
            }
        }
    }

    public void sendMessage(GameNetworkMessage message) {

        if(state != ConnectionState.DISCONNECTED) {

            gameNetworkMessagesSent.incrementAndGet();
            ByteBuffer buffer = messageSerializer.writeToBuffer(message);

            if (!udpMessageProcessor.addReliable(buffer)) {
                if (getState() == ConnectionState.ONLINE) {
                    setState(ConnectionState.DISCONNECTED);
                }
            } else {
                updateLastActivity();
            }
        }
    }

    public List<ByteBuffer> getPendingMessages() {

        List<ByteBuffer> pendingMessageList = new ArrayList<>();

        ByteBuffer buffer;
        while ((buffer = udpMessageProcessor.processNext()) != null) {
            pendingMessageList.add(buffer);
            LOGGER.trace("Sending: {}", SoeMessageUtil.bytesToHex(buffer));
        }

        if(!pendingMessageList.isEmpty()) {
            updateLastActivity();
        }

        return pendingMessageList;
    }

    public void updateLastActivity() {
        lastActivity = System.currentTimeMillis();
    }

    public void updateLastClientActivity() {
        lastClientActivity = System.currentTimeMillis();
    }

    public void sendAck(short sequenceNum) {
        updateLastActivity();
        sendMessage(new AckAllMessage(sequenceNum));
        LOGGER.debug("{} Sending AckAll Sequence {}", id, sequenceNum);
    }

    public void ackAllFromClient(short sequenceNum) {
        clientSequenceNumber.set(sequenceNum);
        udpMessageProcessor.acknowledge(sequenceNum);
        updateLastClientActivity();
        LOGGER.debug("{} Client Ack for Sequence {}", id, sequenceNum);
    }
    
    @Override
    public void setState(ConnectionState state) {
        this.state = state;
    }

    @Override
    public ConnectionState getState() {
        return state;
    }

    public ByteBuffer addIncomingFragment(ByteBuffer buffer) {
        return incomingFragmentContainer.addFragment(buffer);
    }

    public void connect() {
        if(getState() == ConnectionState.ONLINE) {
            confirm();
            return;
        }

        ConnectMessage connectMessage = new ConnectMessage(configuration.getProtocolVersion(), id, configuration.getMaxRawPacketSize());
        sendMessage(connectMessage);
    }

    public void confirm() {
        setState(ConnectionState.ONLINE);
        if(connectCallback != null) {
            connectCallback.accept(this);
        }
    }

    public void terminate(TerminateReason reason) {
        terminate(reason, false);
    }

    public void terminate(TerminateReason reason, boolean silent) {
        if(!silent) {
            Terminate terminate = new Terminate(this.getId(), reason);
            sendMessage(terminate);
        }
        
        setState(ConnectionState.DISCONNECTED);
        terminateReason = reason;
    }

    public List<ConnectionRole> getRoles() {
        return roles;
    }

    public void addRole(ConnectionRole role) {
        roles.add(role);
    }

    public boolean hasRole(ConnectionRole role) {
        for (int i = 0, size = roles.size(); i < size; ++i) {
            if (roles.get(i) == role)
                return true;
        }

        return false;
    }

    public boolean isGod() {
        return hasRole(ConnectionRole.GOD);
    }

    public void updatePingData(final int masterPingTime, final int averagePingTime, final int lowPingTime, final int highPingTime, final int lastPingTime) {
        this.masterPingTime = masterPingTime;
        this.averagePingTime = averagePingTime;
        this.lowPingTime = lowPingTime;
        this.highPingTime = highPingTime;
        this.lastPingTime = lastPingTime;
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
//    013CD4B0	UdpConnection::Init(UdpManager *,UdpIpAddress,int)
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
