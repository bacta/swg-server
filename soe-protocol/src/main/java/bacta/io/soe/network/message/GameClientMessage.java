/*
 * Copyright 2017. Bacta
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package bacta.io.soe.network.message;

import bacta.io.buffer.BufferUtil;
import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
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