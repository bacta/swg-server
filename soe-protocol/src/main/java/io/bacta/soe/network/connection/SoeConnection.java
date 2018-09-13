package io.bacta.soe.network.connection;

import io.bacta.engine.network.connection.Connection;
import io.bacta.engine.network.connection.ConnectionState;
import io.bacta.shared.GameNetworkMessage;
import io.bacta.soe.network.message.EncryptMethod;
import io.bacta.soe.network.message.SoeMessage;
import io.bacta.soe.network.message.TerminateReason;

import java.util.List;
import java.util.function.Consumer;

public interface SoeConnection extends Connection {
    List<ConnectionRole> getRoles();

    void addRole(ConnectionRole role);

    boolean hasRole(ConnectionRole role);

    boolean isGod();

    void sendMessage(SoeMessage message);

    void sendMessage(GameNetworkMessage message);

    ConnectionState getState();

    void setState(ConnectionState state);

    void connect(Consumer<SoeUdpConnection> connectCallback);

    void disconnect(TerminateReason reason);

    void disconnect(TerminateReason reason, boolean sendTerminate);

    boolean isConnected();

    void confirmed(int connectionID, int encryptCode, byte crcBytes, EncryptMethod encryptMethod1, EncryptMethod encryptMethod2, int maxRawPacketSize);

    SoeUdpConnection getSoeUdpConnection();

    int getBactaId();

    void setBactaId(final int id);

    String getBactaUsername();

    void setBactaUsername(final String username);

    long getCurrentNetworkId();

    String getCurrentCharName();

    void logReceivedMessage(GameNetworkMessage incomingMessage);
}
