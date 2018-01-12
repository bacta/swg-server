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

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.engine.buffer.ByteBufferWritable;
import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.Getter;

import java.nio.ByteBuffer;
import java.util.Set;
import java.util.TreeSet;

/**
 * LoginServer->SwgClient
 * Lists the galaxies registered with the LoginServer.
 */

@Priority(0x2)
@Getter
public class LoginEnumCluster extends GameNetworkMessage {

    private final Set<ClusterData> clusterDataSet;
    private int maxCharactersPerAccount;

    public LoginEnumCluster() {
        clusterDataSet = new TreeSet<>();
        maxCharactersPerAccount = 2;
    }

	public LoginEnumCluster(final Set<ClusterData> clusterServerSet, final int maxCharactersPerAccount) {
        this.clusterDataSet = clusterServerSet;
        this.maxCharactersPerAccount = maxCharactersPerAccount;
	}

    public LoginEnumCluster(final ByteBuffer buffer) {
        clusterDataSet = BufferUtil.getTreeSet(buffer, ClusterData::new);
        maxCharactersPerAccount = buffer.getInt();
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        BufferUtil.put(buffer, clusterDataSet);
        buffer.putInt(maxCharactersPerAccount);
    }

    @Getter
    public static final class ClusterData implements ByteBufferWritable, Comparable<ClusterData> {

        private final int id;
        private final String name;
        private final int timezone;  // Offset from GMT in seconds

        public ClusterData(final int id, final String name, final int timezone) {
            this.id = id;
            this.name = name;
            this.timezone = timezone;
        }

        public ClusterData(final ByteBuffer buffer) {
            id = buffer.getInt();
            name = BufferUtil.getAscii(buffer);
            timezone = buffer.getInt();
        }

        @Override
        public void writeToBuffer(final ByteBuffer buffer) {
            buffer.putInt(id);
            BufferUtil.putAscii(buffer, name);
            buffer.putInt(timezone);
        }

        @Override
        public int compareTo(final ClusterData o) {
            return Integer.compare(id, o.getId());
        }
    }
}
