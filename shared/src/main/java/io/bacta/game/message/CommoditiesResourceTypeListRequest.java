package io.bacta.game.message;

import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
      00 00 

  SOECRC32.hashCode(CommoditiesResourceTypeListRequest.class.getSimpleName()); // 0xcb1ae82d
  */
@Getter
@Priority(0x2)
public final class CommoditiesResourceTypeListRequest extends GameNetworkMessage {

    public CommoditiesResourceTypeListRequest() {

    }

    public CommoditiesResourceTypeListRequest(final ByteBuffer buffer) {

    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {

    }
}
