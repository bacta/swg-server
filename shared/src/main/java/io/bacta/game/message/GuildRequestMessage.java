package io.bacta.game.message;

import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
      02 00 F7 4E EB 81 01 E8 76 48 17 00 00 00 

  SOECRC32.hashCode(GuildRequestMessage.class.getSimpleName()); // 0x81eb4ef7
  */
@Getter
@Priority(0x2)
public final class GuildRequestMessage extends GameNetworkMessage {

    public GuildRequestMessage() {

    }

    public GuildRequestMessage(final ByteBuffer buffer) {

    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {

    }
}
