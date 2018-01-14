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
 * SessionServer->SessionClient
 * <p>
 * Responds to a request to establish a session, indicating if the session was established successfully, and returning
 * a sessionId to be used for future requests.
 * <p>
 * The request for this message is {@link EstablishSession}.
 */
@Getter
@AllArgsConstructor
public final class EstablishSessionResponse extends GameNetworkMessage {
    private final int requestId;
    private final SessionResult result;
    private final int bactaId;
    private final String sessionId;

    public EstablishSessionResponse(ByteBuffer buffer) {
        requestId = buffer.getInt();
        result = SessionResult.from(buffer.getInt());
        bactaId = buffer.getInt();
        sessionId = BufferUtil.getAscii(buffer);
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        BufferUtil.put(buffer, requestId);
        BufferUtil.put(buffer, result.getValue());
        BufferUtil.put(buffer, bactaId);
        BufferUtil.putAscii(buffer, sessionId);
    }
}