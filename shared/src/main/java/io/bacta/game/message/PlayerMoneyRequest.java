package io.bacta.game.message;

import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
      01 00 A1 5A 10 9D 

  SOECRC32.hashCode(PlayerMoneyRequest.class.getSimpleName()); // 0x9d105aa1
  */
@Getter
@Priority(0x1)
public final class PlayerMoneyRequest extends GameNetworkMessage {

    public PlayerMoneyRequest() {

    }

    public PlayerMoneyRequest(final ByteBuffer buffer) {

    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {

    }
}
