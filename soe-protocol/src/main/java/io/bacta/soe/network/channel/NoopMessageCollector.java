package io.bacta.soe.network.channel;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.shared.GameNetworkMessage;
import io.bacta.soe.network.connection.LoggingSoeUdpConnection;
import io.bacta.soe.network.connection.SoeUdpConnection;
import io.bacta.soe.network.dispatch.SoeMessageDispatcher;
import io.bacta.soe.network.relay.GameNetworkMessageRelay;
import io.bacta.soe.serialize.GameNetworkMessageSerializer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.nio.ByteBuffer;

@Component
@Slf4j
public class NoopMessageCollector implements SoeChannelMessageCollector {

    private final SoeMessageDispatcher dispatcher;
    private final GameNetworkMessageSerializer serializer;

    @Inject
    public NoopMessageCollector(final SoeMessageDispatcher dispatcher, final GameNetworkMessageSerializer serializer) {
        this.dispatcher = dispatcher;
        this.serializer = serializer;
    }

    @Override
    public void onReceive(SoeUdpConnection connection, ByteBuffer message) {
        logMessage(connection, message, "Receiving");
    }

    @Override
    public void onSend(SoeUdpConnection connection, ByteBuffer message) {
        logMessage(connection, message, "Sending");
    }

    private void logMessage(SoeUdpConnection connection, ByteBuffer message, String type) {
        try {
            MDC.put("connection", String.valueOf(connection.getId()));
            MDC.put("sendType", type);

            ByteBuffer copy = message.duplicate();
            copy.position(0);
            LOGGER.info("Whole Packet: {}", BufferUtil.bytesToHex(copy));

            dispatcher.dispatch(new LoggingSoeUdpConnection(connection), copy, new GameNetworkMessageRelay() {
                        @Override
                        public void receiveMessage(SoeUdpConnection connection, GameNetworkMessage gameNetworkMessage) {
                            logMessageInternal(gameNetworkMessage);
                        }

                        @Override
                        public void sendMessage(int connectionId, GameNetworkMessage message) {

                        }
                    });
        } catch(Exception e) {
            LOGGER.error("Unhandle exception in connection logging", e);
        }
    }

    private void logMessageInternal(GameNetworkMessage gameNetworkMessage) {
        LOGGER.info("{}", gameNetworkMessage.getClass().getSimpleName());
        LOGGER.info("Message: {}", BufferUtil.bytesToHex(serializer.writeToBuffer(gameNetworkMessage)));
    }

    @Override
    public void onReceiveEncrypted(SoeUdpConnection connection, ByteBuffer message) {
        // Do nothing, we don't care about encrypted messages here
    }

    @Override
    public void onSendEncrypted(SoeUdpConnection connection, ByteBuffer message) {
        // Do nothing, we don't care about encrypted messages here
    }
}
