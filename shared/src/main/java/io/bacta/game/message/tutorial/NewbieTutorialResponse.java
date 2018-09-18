package io.bacta.game.message.tutorial;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.ByteBuffer;

/**
 * 0B 00 63 6C 69 65 6E 74 52 65 61 64 79
 * <p>
 * SOECRC32.hashCode(NewbieTutorialResponse.class.getSimpleName()); // 0xca88fbad
 */
@Getter
@Priority(0x2)
@RequiredArgsConstructor
public final class NewbieTutorialResponse extends GameNetworkMessage {
    private final String response;

    public NewbieTutorialResponse(final ByteBuffer buffer) {
        response = BufferUtil.getAscii(buffer);
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        BufferUtil.putAscii(buffer, response);
    }
}
