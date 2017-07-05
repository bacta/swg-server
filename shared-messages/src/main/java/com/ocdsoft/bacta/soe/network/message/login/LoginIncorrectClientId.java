package com.ocdsoft.bacta.soe.network.message.login;

import com.ocdsoft.bacta.engine.buffer.BufferUtil;
import com.ocdsoft.bacta.soe.network.message.GameNetworkMessage;
import com.ocdsoft.bacta.soe.network.message.game.Priority;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
 * Created by crush on 6/8/2017.
 *
 * Sent to the client when the client attempts to login with a client that does not match the server version.
 */
@Getter
@AllArgsConstructor
@Priority(0x4)
public final class LoginIncorrectClientId extends GameNetworkMessage {
    /**
     * This is a datetime timestamp that was made and constant at compilation time. The format of the date is:
     * YYYYMMDD-HH:MM
     *
     * These values were only used when the server was running in debug mode.
     */
    private final String serverId; //GameNetworkMessage::NetworkVersionId
    /**
     * The application version of the server.
     *
     * These values were only used when the server was running in debug mode.
     */
    private final String serverApplicationVersion; //ApplicationVersion//getInternalVersion();

    public LoginIncorrectClientId(final ByteBuffer buffer) {
        serverId = BufferUtil.getAscii(buffer);
        serverApplicationVersion = BufferUtil.getAscii(buffer);
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        BufferUtil.putAscii(buffer, serverId);
        BufferUtil.putAscii(buffer, serverApplicationVersion);
    }
}
