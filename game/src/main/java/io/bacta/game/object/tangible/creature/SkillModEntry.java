package io.bacta.game.object.tangible.creature;

import io.bacta.engine.buffer.ByteBufferWritable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

@Getter
@AllArgsConstructor
public class SkillModEntry implements ByteBufferWritable {

    private final int modifier;
    private final int bonus;

    public SkillModEntry(ByteBuffer buffer) {
        modifier = buffer.getInt();
        bonus = buffer.getInt();
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        buffer.putInt(modifier);
        buffer.putInt(bonus);
    }
}
