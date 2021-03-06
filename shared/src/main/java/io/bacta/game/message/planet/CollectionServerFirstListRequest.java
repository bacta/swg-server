package io.bacta.game.message.planet;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
      00 00 

  SOECRC32.hashCode(CollectionServerFirstListRequest.class.getSimpleName()); // 0x4f686fd9
  */
@Getter
@Priority(0x2)
@AllArgsConstructor
public final class CollectionServerFirstListRequest extends GameNetworkMessage {
    private final String updateNumber;

    public CollectionServerFirstListRequest(final ByteBuffer buffer) {
        this.updateNumber = BufferUtil.getAscii(buffer);
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        BufferUtil.putAscii(buffer, updateNumber);
    }
}
