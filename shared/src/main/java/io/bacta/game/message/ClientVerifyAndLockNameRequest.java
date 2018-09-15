package io.bacta.game.message;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.ByteBuffer;

/**
      27 00 6F 62 6A 65 63 74 2F 63 72 65 61 74 75 72
    65 2F 70 6C 61 79 65 72 2F 68 75 6D 61 6E 5F 66
    65 6D 61 6C 65 2E 69 66 66 0B 00 00 00 52 00 61
    00 6E 00 64 00 6F 00 6D 00 20 00 4E 00 61 00 6D
    00 65 00 00 7C 69 

  SOECRC32.hashCode(ClientVerifyAndLockNameRequest.class.getSimpleName()); // 0x9eb04b9f
  */
@Getter
@Priority(0x3)
@RequiredArgsConstructor
public final class ClientVerifyAndLockNameRequest extends GameNetworkMessage {
    private final String templateName;
    private final String characterName;

    public ClientVerifyAndLockNameRequest(final ByteBuffer buffer) {
        templateName = BufferUtil.getAscii(buffer);
        characterName = BufferUtil.getUnicode(buffer);
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        BufferUtil.putAscii(buffer, templateName);
        BufferUtil.putUnicode(buffer, characterName);
    }
}
