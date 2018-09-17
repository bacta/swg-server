package io.bacta.game.message;

import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
      00 00 

  SOECRC32.hashCode(CommoditiesItemTypeListRequest.class.getSimpleName()); // 0x48f493c5
  */
@Getter
@Priority(0x2)
public final class CommoditiesItemTypeListRequest extends GameNetworkMessage {

    public CommoditiesItemTypeListRequest() {

    }

    public CommoditiesItemTypeListRequest(final ByteBuffer buffer) {

    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {

    }
}
