package io.bacta.game.message;

import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
      02 00 7B 75 24 88 0D E8 76 48 17 00 00 00 00 

  SOECRC32.hashCode(ShowBackpack.class.getSimpleName()); // 0x8824757b
  */
@Getter
@AllArgsConstructor
@Priority(0x2)
public final class ShowBackpack extends GameNetworkMessage {

    public ShowBackpack(final ByteBuffer buffer) {

    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {

    }
}
