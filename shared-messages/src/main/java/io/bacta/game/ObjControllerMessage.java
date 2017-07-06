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

package io.bacta.game;

import io.bacta.buffer.BufferUtil;
import io.bacta.shared.GameNetworkMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;

@Getter
@AllArgsConstructor
public final class ObjControllerMessage extends GameNetworkMessage {
    private final int flags;
    private final int messageType;
    private final long actorNetworkId;
    private final float value;

    @Setter
    private MessageQueueData data;

    public ObjControllerMessage(final ByteBuffer buffer) {
        this.flags = buffer.getInt();
        this.messageType = buffer.getInt();
        this.actorNetworkId = buffer.getLong();
        this.value = buffer.getFloat();

        //NOTICE: MessageQueueData is handled by ObjControllerMessageSerializer
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        BufferUtil.put(buffer, flags);
        BufferUtil.put(buffer, messageType);
        BufferUtil.put(buffer, actorNetworkId);
        BufferUtil.put(buffer, value);

        if (data != null)
            BufferUtil.put(buffer, data);
    }
}