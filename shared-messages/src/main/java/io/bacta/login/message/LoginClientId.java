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
    00 09 00 00 04 00 96 1F 13 41 04 00 61 73 64 66
    00 00 0E 00 32 30 30 35 30 34 30 38 2D 31 38 3A
    30 30 00 FC 79
  */
@Getter
@AllArgsConstructor
@Priority(0x4)
public final class LoginClientId extends GameNetworkMessage {
    /**
     * Serves as the id if logging in through the client directly. If the launchpad has already gained a key for
     * login, then the id can function as an integer value specifying the requested admin level of the player.
     */
    private final String id;
    /**
     * Serves as the key if logging in through the client directly. Otherwise, this should be a session key that
     * the launchpad has already gained from the login server.
     */
    private final String key;
    private final String clientVersion;

    public LoginClientId(final ByteBuffer buffer) {
        id = BufferUtil.getAscii(buffer);
        key = BufferUtil.getAscii(buffer);
        clientVersion = BufferUtil.getAscii(buffer);
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        BufferUtil.putAscii(buffer, id);
        BufferUtil.putAscii(buffer, key);
        BufferUtil.putAscii(buffer, clientVersion);
    }
}
