package io.bacta.game.message;

import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
      5A 00 00 00 

  SOECRC32.hashCode(SetFurnitureRotationDegree.class.getSimpleName()); // 0x91419229
  */
@Getter
@Priority(0x2)
public final class SetFurnitureRotationDegree extends GameNetworkMessage {

    public SetFurnitureRotationDegree() {

    }

    public SetFurnitureRotationDegree(final ByteBuffer buffer) {

    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {

    }
}
