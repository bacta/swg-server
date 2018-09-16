package io.bacta.game.message;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import io.bacta.shared.localization.StringId;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.ByteBuffer;

@Getter
@Priority(0x03)
@RequiredArgsConstructor
public final class ClientVerifyAndLockNameResponse extends GameNetworkMessage {
    private final String characterName;
    private final StringId errorMessage;

    public ClientVerifyAndLockNameResponse(ByteBuffer buffer) {
        characterName = BufferUtil.getUnicode(buffer);
        errorMessage = new StringId(buffer);
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        BufferUtil.putUnicode(buffer, characterName);
        BufferUtil.put(buffer, errorMessage);
    }
}
