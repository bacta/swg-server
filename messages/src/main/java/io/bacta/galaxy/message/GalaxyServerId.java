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
 * When a galaxy server comes online, it must announce itself to any login servers that it wants to service it. This
 * message is step one in the process. It will send this message, and then the respective login server will tell the
 * galaxy server its unique identifier in the galaxy cluster.
 *
 * This message should also be sent following a galaxy server receiving a {@link io.bacta.login.message.LoginServerOnline}
 * message as it kicks off the process of identifying with the login server.
 */
@Getter
@Priority(0x02)
@AllArgsConstructor
public final class GalaxyServerId extends GameNetworkMessage {
    /**
     * The name of the galaxy.
     */
    private final String galaxyName;
    /**
     * The timezone for the galaxy in seconds.
     */
    private final int timeZone;
    /**
     * The version of the game network that the galaxy is using.
     */
    private final String networkVersion;

    public GalaxyServerId(final ByteBuffer buffer) {
        galaxyName = BufferUtil.getAscii(buffer);
        timeZone = buffer.getInt();
        networkVersion = BufferUtil.getAscii(buffer);
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        BufferUtil.put(buffer, galaxyName);
        BufferUtil.put(buffer, timeZone);
        BufferUtil.put(buffer, networkVersion);
    }
}
