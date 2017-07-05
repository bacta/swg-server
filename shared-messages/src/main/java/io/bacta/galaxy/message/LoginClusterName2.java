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

import bacta.io.buffer.BufferUtil;
import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
 * Created by crush on 7/4/2017.
 *
 * Sent from the GalaxyServer to the LoginServer telling it which cluster it is servicing. Also contains some additional
 * data about the cluster.
 */
@Getter
@Priority(0x03)
@AllArgsConstructor
public final class LoginClusterName2 extends GameNetworkMessage {
    private final String clusterName;
    private final int timeZone;
    private final String branch;
    private final int changelist;
    private final String networkVersion;

    public LoginClusterName2(final ByteBuffer buffer) {
        clusterName = BufferUtil.getAscii(buffer);
        timeZone = buffer.getInt();
        branch = BufferUtil.getAscii(buffer);
        changelist = buffer.getInt();
        networkVersion = BufferUtil.getAscii(buffer);
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        BufferUtil.put(buffer, clusterName);
        BufferUtil.put(buffer, timeZone);
        BufferUtil.put(buffer, branch);
        BufferUtil.put(buffer, changelist);
        BufferUtil.put(buffer, networkVersion);
    }
}
