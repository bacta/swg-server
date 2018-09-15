package io.bacta.game.message;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
 * Response to LagRequest packet.
 * Details the lag in milliseconds between the client and the connection server and game server.
 * SOE throttled the requests.
 */
@Getter
@Priority(0x04)
public final class LagReport extends GameNetworkMessage {
    private final int connectionServerLag;
    private final int gameServerLag;

    public LagReport(ByteBuffer buffer) {
        connectionServerLag = buffer.getInt();
        gameServerLag = buffer.getInt();
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        BufferUtil.put(buffer, connectionServerLag);
        BufferUtil.put(buffer, gameServerLag);
    }
}
