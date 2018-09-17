package io.bacta.game.message;

import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
      00 00 

  SOECRC32.hashCode(SetJediSlotInfo.class.getSimpleName()); // 0x6e295022
  */
@Getter
@Priority(0x2)
public final class SetJediSlotInfo extends GameNetworkMessage {

    public SetJediSlotInfo() {

    }

    public SetJediSlotInfo(final ByteBuffer buffer) {

    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {

    }
}
