package io.bacta.login.server;

import io.bacta.shared.GameNetworkMessage;
import io.bacta.soe.network.connection.SoeUdpConnection;
import io.bacta.soe.network.connection.interceptor.SoeUdpConnectionOrderedMessageInterceptor;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

class TestAwaitMessageInterceptor implements SoeUdpConnectionOrderedMessageInterceptor {

    List<ByteBuffer> protocolMessageList = new ArrayList<>();
    List<GameNetworkMessage> messageList = new ArrayList<>();

    @Override
    public void incomingProtocol(SoeUdpConnection connection, ByteBuffer decodedMessage) {
        protocolMessageList.add(decodedMessage);
    }

    @Override
    public void incomingGameNetworkMessage(SoeUdpConnection connection, GameNetworkMessage gameNetworkMessage) {
        messageList.add(gameNetworkMessage);
    }

    public <T extends GameNetworkMessage> T getReceivedMessage(Class<T> messageClass) {
        for(GameNetworkMessage message : messageList) {
            if(message.getClass().equals(messageClass)) {
                messageList.remove(message);
                return (T) message;
            }
        }
        return null;
    }

    boolean hasMessage(Class<? extends GameNetworkMessage> messageClass) {
        for(GameNetworkMessage message : messageList) {
            if(message.getClass().equals(messageClass)) {
                return true;
            }
        }
        return false;
    }
}
