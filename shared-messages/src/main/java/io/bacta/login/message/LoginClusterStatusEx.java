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

import bacta.io.buffer.BufferUtil;
import bacta.io.buffer.ByteBufferWritable;
import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;
import java.util.Set;

/**
 * Created by kyle on 5/30/2016.
 */
@Getter
@Priority(0x05)
@AllArgsConstructor
public final class LoginClusterStatusEx extends GameNetworkMessage {
    private final Set<ClusterData> extendedClusterDataSet;

    public LoginClusterStatusEx(final ByteBuffer buffer) {
        extendedClusterDataSet = BufferUtil.getTreeSet(buffer, ClusterData::new);
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        BufferUtil.put(buffer, extendedClusterDataSet);
    }

    /**
     * Represents a Cluster's Extended data as it is represented across the network.
     * This is a data transfer object.
     */
    @Getter
    @AllArgsConstructor
    public static final class ClusterData implements ByteBufferWritable {
        private final int clusterId;
        private final String branch;
        private final String networkVersion;
        private final int version;
        private final int reserved1;
        private final int reserved2;
        private final int reserved3;
        private final int reserved4;

        public ClusterData(final ByteBuffer buffer) {
            this.clusterId = buffer.getInt();
            this.branch = BufferUtil.getAscii(buffer);
            this.networkVersion = BufferUtil.getAscii(buffer);
            this.version = buffer.getInt();
            this.reserved1 = buffer.getInt();
            this.reserved2 = buffer.getInt();
            this.reserved3 = buffer.getInt();
            this.reserved4 = buffer.getInt();

        }

        @Override
        public void writeToBuffer(ByteBuffer buffer) {
            BufferUtil.put(buffer, clusterId);
            BufferUtil.put(buffer, branch);
            BufferUtil.put(buffer, networkVersion);
            BufferUtil.put(buffer, version);
            BufferUtil.put(buffer, reserved1);
            BufferUtil.put(buffer, reserved2);
            BufferUtil.put(buffer, reserved3);
            BufferUtil.put(buffer, reserved4);
        }
    }
}
