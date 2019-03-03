package io.bacta.game.message;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.shared.GameNetworkMessage;
import io.bacta.shared.math.Vector;
import lombok.AllArgsConstructor;

import java.nio.ByteBuffer;

/**
 * Created by kyle on 5/19/2016.
 */

@AllArgsConstructor
public class CmdStartScene extends GameNetworkMessage {
    private final long networkId;
    private final String sceneName;
    private final Vector startPosition;
    private final float startYaw;
    private final String templateName;
    private final long serverTime;
    private final int serverEpoch;
    private final boolean ignoreLayoutFiles;

    public CmdStartScene(final ByteBuffer buffer) {
        ignoreLayoutFiles = BufferUtil.getBoolean(buffer);
        networkId = buffer.getLong();
        sceneName = BufferUtil.getAscii(buffer);
        startPosition = new Vector(buffer);
        startYaw = buffer.getFloat();
        templateName = BufferUtil.getAscii(buffer);
        serverTime = buffer.getLong();
        serverEpoch = buffer.getInt();
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        BufferUtil.put(buffer, ignoreLayoutFiles);
        buffer.putLong(networkId);
        BufferUtil.putAscii(buffer, sceneName);
        startPosition.writeToBuffer(buffer);
        buffer.putFloat(startYaw);
        BufferUtil.putAscii(buffer, templateName);
        buffer.putLong(serverTime);
        buffer.putInt(serverEpoch);
    }
}
