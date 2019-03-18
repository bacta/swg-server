package io.bacta.game.message;

import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
      01 00 81 3B B0 C1 

  SOECRC32.hashCode(FactionRequestMessage.class.getSimpleName()); // 0xc1b03b81
  */
@Getter
@Priority(0x1)
public final class FactionRequestMessage extends GameNetworkMessage {

    public FactionRequestMessage() {

    }

    public FactionRequestMessage(final ByteBuffer buffer) {

    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {

    }
}
