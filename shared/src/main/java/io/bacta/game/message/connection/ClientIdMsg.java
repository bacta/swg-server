package io.bacta.game.message.connection;


import io.bacta.engine.buffer.BufferUtil;
import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;


@Getter
@Priority(0x4)
@AllArgsConstructor
public class ClientIdMsg extends GameNetworkMessage {

    private final int gameBitsToClear;
    private final String token;
    private final String clientVersion;

    public ClientIdMsg(ByteBuffer buffer) {
        gameBitsToClear = buffer.getInt();
        token = BufferUtil.getBinaryString(buffer);
        clientVersion = BufferUtil.getAscii(buffer);
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        buffer.putInt(gameBitsToClear);
        BufferUtil.putBinaryString(buffer, token);
        BufferUtil.putAscii(buffer, clientVersion);
    }
}
