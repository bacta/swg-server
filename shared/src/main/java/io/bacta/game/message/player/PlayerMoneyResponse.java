package io.bacta.game.message.player;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.ByteBuffer;

@Getter
@Priority(0x02)
@RequiredArgsConstructor
public final class PlayerMoneyResponse extends GameNetworkMessage {
    private final int cashBalance;
    private final int bankBalance;

    public PlayerMoneyResponse(ByteBuffer buffer) {
        this.cashBalance = buffer.getInt();
        this.bankBalance = buffer.getInt();
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        BufferUtil.put(buffer, cashBalance);
        BufferUtil.put(buffer, bankBalance);
    }
}
