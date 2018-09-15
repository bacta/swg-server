package io.bacta.game.message;

import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
      00 

  SOECRC32.hashCode(28afefcc187a11dc888b001.class.getSimpleName()); // 0x173b91c2
  */
@Getter
@Priority(0x2)
public final class GodClientMessage extends GameNetworkMessage {

    public GodClientMessage() {

    }

    public GodClientMessage(final ByteBuffer buffer) {

    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {

    }
}
