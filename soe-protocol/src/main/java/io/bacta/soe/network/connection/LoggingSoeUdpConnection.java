package io.bacta.soe.network.connection;

import io.bacta.engine.network.connection.ConnectionState;
import io.bacta.shared.GameNetworkMessage;
import io.bacta.soe.network.message.EncryptMethod;
import io.bacta.soe.network.message.SoeMessage;
import io.bacta.soe.network.message.TerminateReason;

import javax.management.ObjectName;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.Consumer;

public class LoggingSoeUdpConnection implements SoeUdpConnection {

    private final SoeUdpConnection internalConnection;

    public LoggingSoeUdpConnection(SoeUdpConnection connection) {
        this.internalConnection = connection;
    }

    @Override
    public void updateLastActivity() {

    }

    @Override
    public void updateLastClientActivity() {

    }

    @Override
    public void sendMessage(SoeMessage message) {

    }

    @Override
    public void sendMessage(GameNetworkMessage message) {

    }

    @Override
    public List<ByteBuffer> getPendingMessages() {
        return null;
    }

    @Override
    public void ackClient(short sequenceNum) {

    }

    @Override
    public void ackAllFromClient(short sequenceNum) {

    }

    @Override
    public void connect(Consumer<SoeUdpConnection> connectCallback) {

    }

    @Override
    public void doConfirm(int connectionId, int encryptCode, int maxRawPacketSize, EncryptMethod encryptMethod1, EncryptMethod encryptMethod2) {

    }

    @Override
    public void confirmed(int connectionID, int encryptCode, byte crcBytes, EncryptMethod encryptMethod1, EncryptMethod encryptMethod2, int maxRawPacketSize) {

    }

    @Override
    public void terminate(TerminateReason reason) {

    }

    @Override
    public void terminate(TerminateReason reason, boolean silent) {

    }

    @Override
    public void updatePingData(int masterPingTime, int averagePingTime, int lowPingTime, int highPingTime, int lastPingTime) {

    }

    @Override
    public long getProtocolMessagesSent() {
        return 0;
    }

    @Override
    public long getProtocolMessagesReceived() {
        return 0;
    }

    @Override
    public void logReceivedMessage(GameNetworkMessage incomingMessage) {

    }

    @Override
    public ObjectName getObjectName() {
        return null;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return null;
    }

    @Override
    public int getId() {
        return internalConnection.getId();
    }

    @Override
    public int getProtocolVersion() {
        return 0;
    }

    @Override
    public byte getCrcBytes() {
        return 0;
    }

    @Override
    public boolean isCompression() {
        return false;
    }

    @Override
    public IncomingMessageProcessor getIncomingMessageProcessor() {
        return new LoggingIncomingMessageProcessor(internalConnection.getIncomingMessageProcessor());
    }

    @Override
    public OutgoingMessageProcessor getOutgoingMessageQueue() {
        return null;
    }

    @Override
    public int getMaxRawPacketSize() {
        return 0;
    }

    @Override
    public int getEncryptCode() {
        return 0;
    }

    @Override
    public EncryptMethod getEncryptMethod1() {
        return null;
    }

    @Override
    public EncryptMethod getEncryptMethod2() {
        return null;
    }

    @Override
    public long getLastActivity() {
        return 0;
    }

    @Override
    public long getLastRemoteActivity() {
        return 0;
    }

    @Override
    public TerminateReason getTerminateReason() {
        return null;
    }

    @Override
    public ConnectionState getConnectionState() {
        return null;
    }

    @Override
    public int getMasterPingTime() {
        return 0;
    }

    @Override
    public int getAveragePingTime() {
        return 0;
    }

    @Override
    public int getLowPingTime() {
        return 0;
    }

    @Override
    public int getHighPingTime() {
        return 0;
    }

    @Override
    public int getLastPingTime() {
        return 0;
    }

    @Override
    public Consumer<SoeUdpConnection> getConnectCallback() {
        return null;
    }

    @Override
    public void setConnectionState(ConnectionState connectionState) {

    }
}
