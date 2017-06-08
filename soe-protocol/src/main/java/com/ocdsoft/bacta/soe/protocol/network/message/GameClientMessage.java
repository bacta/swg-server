package com.ocdsoft.bacta.soe.protocol.network.message;

import com.ocdsoft.bacta.engine.buffer.BufferUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by crush on 5/23/2016.
 * <p>
 * This com.ocdsoft.bacta.swg.login.message wraps a GameNetworkMessage that was sent from the client, so that it may be passed on to another
 * server. It holds a list of networkIds which should receive the response to the com.ocdsoft.bacta.swg.login.message.
 */
@Getter
@Priority(0x05)
@AllArgsConstructor
public final class GameClientMessage extends GameNetworkMessage {
    private final long[] distributionList;
    private final boolean reliable;
    private final ByteBuffer internalMessage;

    public GameClientMessage(final ByteBuffer buffer) {
        final int size = buffer.getInt();
        distributionList = new long[size];

        for (int i = 0; i < size; i++)
            distributionList[i] = buffer.getLong();

        reliable = BufferUtil.getBoolean(buffer);
        internalMessage = buffer.slice().order(ByteOrder.LITTLE_ENDIAN);
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        final int size = distributionList.length;
        BufferUtil.put(buffer, size);

        for (int i = 0; i < size; ++i)
            BufferUtil.put(buffer, distributionList[i]);

        BufferUtil.put(buffer, reliable);
        buffer.put(internalMessage);
    }
}