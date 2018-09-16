package io.bacta.game.object.intangible.player;

import io.bacta.engine.buffer.ByteBufferWritable;
import lombok.Getter;

import java.nio.ByteBuffer;

public class DraftSchematicEntry implements ByteBufferWritable {
    @Getter
    private final int serverObjectCrc = 0;
    @Getter
    private final int clientObjectCrc = 0;

    public DraftSchematicEntry(final ByteBuffer buffer) {

    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        throw new UnsupportedOperationException("Not implemented");
    }
}