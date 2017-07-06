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

package io.bacta.login.message;

import io.bacta.buffer.BufferUtil;
import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
 * Created by crush on 6/8/2017.
 *
 * Sent to the client when the client attempts to login with a client that does not match the server version.
 */
@Getter
@AllArgsConstructor
@Priority(0x4)
public final class LoginIncorrectClientId extends GameNetworkMessage {
    /**
     * This is a datetime timestamp that was made and constant at compilation time. The format of the date is:
     * YYYYMMDD-HH:MM
     *
     * These values were only used when the server was running in debug mode.
     */
    private final String serverId; //GameNetworkMessage::NetworkVersionId
    /**
     * The application version of the server.
     *
     * These values were only used when the server was running in debug mode.
     */
    private final String serverApplicationVersion; //ApplicationVersion//getInternalVersion();

    public LoginIncorrectClientId(final ByteBuffer buffer) {
        serverId = BufferUtil.getAscii(buffer);
        serverApplicationVersion = BufferUtil.getAscii(buffer);
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        BufferUtil.putAscii(buffer, serverId);
        BufferUtil.putAscii(buffer, serverApplicationVersion);
    }
}
