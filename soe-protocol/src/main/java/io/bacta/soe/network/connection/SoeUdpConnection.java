package io.bacta.soe.network.connection;

import io.bacta.engine.network.connection.ConnectionState;
import io.bacta.engine.network.udp.UdpConnection;
import io.bacta.shared.GameNetworkMessage;
import io.bacta.soe.network.connection.interceptor.SoeUdpConnectionOrderedMessageInterceptor;
import io.bacta.soe.network.message.EncryptMethod;
import io.bacta.soe.network.message.SoeMessage;
import io.bacta.soe.network.message.TerminateReason;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.Consumer;

public interface SoeUdpConnection extends UdpConnection {
    void updateLastActivity();

    void updateLastClientActivity();

    void sendMessage(SoeMessage message);

    void sendMessage(GameNetworkMessage message);

    List<ByteBuffer> getPendingMessages();

    void ackClient(short sequenceNum);

    void ackAllFromClient(short sequenceNum);

    void connect();

    void connect(Consumer<SoeUdpConnection> connectCallback);

    void handleConfirm(int connectionId, int encryptCode, int maxRawPacketSize, EncryptMethod encryptMethod1, EncryptMethod encryptMethod2);

    void confirmed(int connectionID, int encryptCode, byte crcBytes, EncryptMethod encryptMethod1, EncryptMethod encryptMethod2, int maxRawPacketSize);

    void terminate(TerminateReason reason);

    void terminate(TerminateReason reason, boolean silent);

    void updatePingData(int masterPingTime, int averagePingTime, int lowPingTime, int highPingTime, int lastPingTime);

    long getProtocolMessagesSent();

    long getProtocolMessagesReceived();

    void logReceivedMessage(GameNetworkMessage incomingMessage);

    javax.management.ObjectName getObjectName();

    java.net.InetSocketAddress getRemoteAddress();

    int getId();

    int getProtocolVersion();

    byte getCrcBytes();

    boolean isCompression();

    IncomingMessageProcessor getIncomingMessageProcessor();

    OutgoingMessageProcessor getOutgoingMessageQueue();

    int getMaxRawPacketSize();

    int getEncryptCode();

    EncryptMethod getEncryptMethod1();

    EncryptMethod getEncryptMethod2();

    long getLastActivity();

    long getLastRemoteActivity();

    TerminateReason getTerminateReason();

    ConnectionState getConnectionState();

    int getMasterPingTime();

    int getAveragePingTime();

    int getLowPingTime();

    int getHighPingTime();

    int getLastPingTime();

    Consumer<SoeUdpConnection> getConnectCallback();

    void setConnectionState(io.bacta.engine.network.connection.ConnectionState connectionState);

    ByteBuffer processIncomingProtocol(ByteBuffer decodedMessage);

    GameNetworkMessage processIncomingGNM(GameNetworkMessage gameNetworkMessage);

    <T extends SoeUdpConnectionOrderedMessageInterceptor> T getInterceptor(Class<T> p);
}
