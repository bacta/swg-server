package io.bacta.game.message;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.shared.GameNetworkMessage;
import io.bacta.shared.tre.math.Vector;
import lombok.AllArgsConstructor;

import java.nio.ByteBuffer;

/**
 * Created by kyle on 5/19/2016.
 */

@AllArgsConstructor
public class CmdStartScene extends GameNetworkMessage {

    private final boolean ignoreLayoutFiles;
    private final long characterId;
    private final String terrain;
    private final Vector vector;
    private final float yaw;
    private final String templateName;
    private final long timeSeconds;
    private final int galacticTime;

    public CmdStartScene(final ByteBuffer buffer) {
        ignoreLayoutFiles = BufferUtil.getBoolean(buffer);
        characterId = buffer.getLong();
        terrain = BufferUtil.getAscii(buffer);
        vector = new Vector(buffer);
        yaw = buffer.getFloat();
        templateName = BufferUtil.getAscii(buffer);
        timeSeconds = buffer.getLong();
        galacticTime = buffer.getInt();
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        BufferUtil.put(buffer, ignoreLayoutFiles);
        buffer.putLong(characterId);
        BufferUtil.putAscii(buffer, terrain);
        vector.writeToBuffer(buffer);
        buffer.putFloat(yaw);
        BufferUtil.putAscii(buffer, templateName);
        buffer.putLong(timeSeconds);
        buffer.putInt(galacticTime);
    }
}
