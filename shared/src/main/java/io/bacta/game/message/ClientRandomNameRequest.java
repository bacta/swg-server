package io.bacta.game.message;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
      25 00 6F 62 6A 65 63 74 2F 63 72 65 61 74 75 72
    65 2F 70 6C 61 79 65 72 2F 68 75 6D 61 6E 5F 6D
    61 6C 65 2E 69 66 66 

  SOECRC32.hashCode(ClientRandomNameRequest.class.getSimpleName()); // 0xd6d1b6d1
  */
@Getter
@Priority(0x2)
public final class ClientRandomNameRequest extends GameNetworkMessage {
    private final String creatureTemplate;

    public ClientRandomNameRequest(final ByteBuffer buffer) {
        this.creatureTemplate = BufferUtil.getAscii(buffer);
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        BufferUtil.putAscii(buffer, creatureTemplate);
    }
}
