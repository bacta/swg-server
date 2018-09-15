package io.bacta.game.message;

import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
      8D 14 A5 D4 E8 00 00 00 

  SOECRC32.hashCode(SelectCharacter.class.getSimpleName()); // 0xb5098d76
  */
@Getter
@Priority(0x2)
public final class SelectCharacter extends GameNetworkMessage {

    public SelectCharacter() {

    }

    public SelectCharacter(final ByteBuffer buffer) {

    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {

    }
}
