package io.bacta.game.message;

import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
      01 00 00 00 

  SOECRC32.hashCode(SetCombatSpamFilter.class.getSimpleName()); // 0xe25d8df0
  */
@Getter
@Priority(0x2)
public final class SetCombatSpamFilter extends GameNetworkMessage {

    public SetCombatSpamFilter() {

    }

    public SetCombatSpamFilter(final ByteBuffer buffer) {

    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {

    }
}
