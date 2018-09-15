package io.bacta.game.message;

import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
      00 1A 09 

  SOECRC32.hashCode(RequestGalaxyLoopTimes.class.getSimpleName()); // 0x7d842d68
  */
@Getter
@Priority(0x1)
public final class RequestGalaxyLoopTimes extends GameNetworkMessage {

    public RequestGalaxyLoopTimes() {

    }

    public RequestGalaxyLoopTimes(final ByteBuffer buffer) {

    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {

    }
}
