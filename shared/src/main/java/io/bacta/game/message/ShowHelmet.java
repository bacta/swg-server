package io.bacta.game.message;

import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
      02 00 1D EC EF DD 0D E8 76 48 17 00 00 00 00 

  SOECRC32.hashCode(ShowHelmet.class.getSimpleName()); // 0xddefec1d
  */
@Getter
@AllArgsConstructor
@Priority(0x2)
public final class ShowHelmet extends GameNetworkMessage {

    public ShowHelmet(final ByteBuffer buffer) {

    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {

    }
}
