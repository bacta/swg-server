package io.bacta.game.message;

import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
      

  SOECRC32.hashCode(CmdSceneReady.class.getSimpleName()); // 0x43fd1c22
  */
@Getter
@Priority(0x1)
public final class CmdSceneReady extends GameNetworkMessage {

    public CmdSceneReady() {

    }

    public CmdSceneReady(final ByteBuffer buffer) {

    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {

    }
}
