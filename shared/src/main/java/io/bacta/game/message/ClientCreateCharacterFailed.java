package io.bacta.game.message;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import io.bacta.shared.tre.localization.StringId;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.ByteBuffer;

@Getter
@Priority(0x04)
@RequiredArgsConstructor
public final class ClientCreateCharacterFailed extends GameNetworkMessage {
    private final String name;
    private final StringId errorMessage;

    public ClientCreateCharacterFailed(ByteBuffer buffer) {
        name = BufferUtil.getUnicode(buffer);
        errorMessage = new StringId(buffer);
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        BufferUtil.putUnicode(buffer, name);
        BufferUtil.put(buffer, errorMessage);
    }
}
