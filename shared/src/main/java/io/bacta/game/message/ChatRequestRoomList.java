package io.bacta.game.message;

import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
      

  SOECRC32.hashCode(ChatRequestRoomList.class.getSimpleName()); // 0x4c3d2cfa
  */
@Getter
@Priority(0x1)
public final class ChatRequestRoomList extends GameNetworkMessage {

    public ChatRequestRoomList() {

    }

    public ChatRequestRoomList(final ByteBuffer buffer) {

    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {

    }
}
