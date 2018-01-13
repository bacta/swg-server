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

package io.bacta.galaxy.message;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
 * GalaxyServer->LoginServer validates that the specified account has permission.
 */
@Getter
@Priority(0x04)
@AllArgsConstructor
public final class ValidateAccount extends GameNetworkMessage {
    private final int bactaId;
    private final int subscriptionBits;

    public ValidateAccount(ByteBuffer buffer) {
        bactaId = buffer.getInt();
        subscriptionBits = buffer.getInt();
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        BufferUtil.put(buffer, bactaId);
        BufferUtil.put(buffer, subscriptionBits);
    }
}
