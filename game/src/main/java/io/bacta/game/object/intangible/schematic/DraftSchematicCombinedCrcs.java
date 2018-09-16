package io.bacta.game.object.intangible.schematic;

import io.bacta.engine.buffer.ByteBufferWritable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
 * Created by crush on 5/9/2016.
 * <p>
 * Encapsualates both the server and shared template crcs for this draft schematic.
 */
@Getter
@AllArgsConstructor
public class DraftSchematicCombinedCrcs implements ByteBufferWritable {
    private final int serverTemplateCrc;
    private final int sharedTemplateCrc;

    public DraftSchematicCombinedCrcs(final ByteBuffer buffer) {
        serverTemplateCrc = buffer.getInt();
        sharedTemplateCrc = buffer.getInt();
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        buffer.putInt(serverTemplateCrc);
        buffer.putInt(sharedTemplateCrc);
    }
}
