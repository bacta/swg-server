package io.bacta.soe.network.connection.interceptor;

import io.bacta.shared.GameNetworkMessage;
import io.bacta.soe.network.connection.SoeUdpConnection;
import io.bacta.soe.network.message.SoeMessageType;
import io.bacta.soe.util.SoeMessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.nio.ByteBuffer;

@Slf4j
public class MessageLoggingInterceptor implements SoeUdpConnectionOrderedMessageInterceptor {

    @Override
    public void incomingProtocol(SoeUdpConnection connection, ByteBuffer buffer) {
        MDC.put("connection", String.valueOf(connection.getId()));
        MDC.put("sendType", "C->S");

        byte type = buffer.get(1);
        if(type < 0 || type > 0x1E) {
            throw new RuntimeException("Type out of range: " + type + " " + buffer.toString() + " " + SoeMessageUtil.bytesToHex(buffer));
        }

        SoeMessageType packetType = SoeMessageType.values()[type];

        MDC.put("connectionLogging", "true");
        LOGGER.info("{} Received Protocol message with content {}", packetType.name(), SoeMessageUtil.bytesToHex(buffer));
        MDC.remove("connectionLogging");
    }

    @Override
    public void incomingGameNetworkMessage(SoeUdpConnection connection, GameNetworkMessage gameNetworkMessage) {
        MDC.put("connection", String.valueOf(connection.getId()));
        MDC.put("sendType", "");

        MDC.put("connectionLogging", "true");
        LOGGER.info("   {} message with content {}", gameNetworkMessage.getClass().getSimpleName(), SoeMessageUtil.bytesToHex(gameNetworkMessage));
        MDC.remove("connectionLogging");
    }

    @Override
    public void outgoingProtocol(SoeUdpConnection connection, ByteBuffer buffer) {
        MDC.put("connection", String.valueOf(connection.getId()));
        MDC.put("sendType", "S->C");

        byte type = buffer.get(1);
        if(type < 0 || type > 0x1E) {
            throw new RuntimeException("Type out of range: " + type + " " + buffer.toString() + " " + SoeMessageUtil.bytesToHex(buffer));
        }

        SoeMessageType packetType = SoeMessageType.values()[type];
        MDC.put("connectionLogging", "true");
        LOGGER.info("{} Received Protocol message with content {}", packetType.name(), SoeMessageUtil.bytesToHex(buffer));
        MDC.remove("connectionLogging");
    }

    @Override
    public void outgoingGameNetworkMessage(SoeUdpConnection connection, GameNetworkMessage gameNetworkMessage) {
        MDC.put("connection", String.valueOf(connection.getId()));
        MDC.put("sendType", "");

        MDC.put("connectionLogging", "true");
        LOGGER.info("   {} message with content {}", gameNetworkMessage.getClass().getSimpleName(), SoeMessageUtil.bytesToHex(gameNetworkMessage));
        MDC.remove("connectionLogging");
    }
}
