package io.bacta.game.message.player;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.ByteBuffer;

@Getter
@Priority(0x04)
@RequiredArgsConstructor
public final class GuildResponseMessage extends GameNetworkMessage {
    private final long id;
    private final String name;
    private final String title;

    public GuildResponseMessage(ByteBuffer buffer) {
        id = buffer.getLong();
        name = BufferUtil.getAscii(buffer);
        title = BufferUtil.getAscii(buffer);
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        BufferUtil.put(buffer, id);
        BufferUtil.put(buffer, name);
        BufferUtil.put(buffer, title);
    }
}
