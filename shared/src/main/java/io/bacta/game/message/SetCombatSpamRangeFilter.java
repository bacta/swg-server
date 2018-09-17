package io.bacta.game.message;

import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
      20 00 00 00 

  SOECRC32.hashCode(SetCombatSpamRangeFilter.class.getSimpleName()); // 0xfc92198b
  */
@Getter
@Priority(0x2)
public final class SetCombatSpamRangeFilter extends GameNetworkMessage {

    public SetCombatSpamRangeFilter() {

    }

    public SetCombatSpamRangeFilter(final ByteBuffer buffer) {

    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {

    }
}
