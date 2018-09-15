package io.bacta.game.message;

import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
      

  SOECRC32.hashCode(LagRequest.class.getSimpleName()); // 0x31805ee0
  */
@Getter
@Priority(0x1)
public final class LagRequest extends GameNetworkMessage {

    public LagRequest() {

    }

    public LagRequest(final ByteBuffer buffer) {

    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {

    }
}
