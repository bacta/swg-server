package io.bacta.game.message;

import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
      0B 00 63 6C 69 65 6E 74 52 65 61 64 79 

  SOECRC32.hashCode(NewbieTutorialResponse.class.getSimpleName()); // 0xca88fbad
  */
@Getter
@Priority(0x2)
public final class NewbieTutorialResponse extends GameNetworkMessage {

    public NewbieTutorialResponse() {

    }

    public NewbieTutorialResponse(final ByteBuffer buffer) {

    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {

    }
}
