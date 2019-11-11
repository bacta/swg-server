package io.bacta.soe.network.connection.interceptor;

import io.bacta.shared.GameNetworkMessage;
import io.bacta.soe.network.connection.SoeUdpConnection;

import java.nio.ByteBuffer;

public interface SoeUdpConnectionOrderedMessageInterceptor {
    default void incomingProtocol(SoeUdpConnection connection, ByteBuffer decodedMessage) {

    }
    default void incomingGameNetworkMessage(SoeUdpConnection connection, GameNetworkMessage gameNetworkMessage) {

    }

    default void outgoingProtocol(SoeUdpConnection connection, ByteBuffer decodedMessage) {

    }
    default void outgoingGameNetworkMessage(SoeUdpConnection connection, GameNetworkMessage gameNetworkMessage) {

    }
}
