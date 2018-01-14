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

package io.bacta.session.message;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.shared.GameNetworkMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
 * SessionClient->SessionServer
 * <p>
 * Request to establish a new session for the given login credentials.
 * <p>
 * The response to this message is {@link EstablishSessionResponse}.
 */
@Getter
@AllArgsConstructor
public final class EstablishSession extends GameNetworkMessage {
    private final int requestId;
    private final String username;
    private final String password;

    public EstablishSession(ByteBuffer buffer) {
        requestId = buffer.getInt();
        username = BufferUtil.getAscii(buffer);
        password = BufferUtil.getAscii(buffer);
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        BufferUtil.put(buffer, requestId);
        BufferUtil.putAscii(buffer, username);
        BufferUtil.putAscii(buffer, password);
    }
}