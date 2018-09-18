package io.bacta.game.message.tutorial;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.ByteBuffer;

@Getter
@Priority(0x02)
@RequiredArgsConstructor
public final class NewbieTutorialRequest extends GameNetworkMessage {
    private final String request;

    public NewbieTutorialRequest(ByteBuffer buffer) {
        request = BufferUtil.getAscii(buffer);
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        BufferUtil.putAscii(buffer, request);
    }
}
