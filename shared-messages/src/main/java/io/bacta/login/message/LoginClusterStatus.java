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
import io.bacta.buffer.ByteBufferWritable;
import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.nio.ByteBuffer;
import java.util.Set;

@Priority(0x3)
public final class LoginClusterStatus extends GameNetworkMessage {
    private final Set<ClusterData> clusterDataSet;

    public LoginClusterStatus(final Set<ClusterData> clusterServerSet) {
        this.clusterDataSet = clusterServerSet;
    }

    public LoginClusterStatus(final ByteBuffer buffer) {
        clusterDataSet = BufferUtil.getTreeSet(buffer, ClusterData::new);
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        BufferUtil.put(buffer, clusterDataSet);
    }

    @Getter
    @AllArgsConstructor
    public static final class ClusterData implements ByteBufferWritable, Comparable<ClusterData> {
        private final int clusterId;
        private final String connectionServerAddress;
        private final short connectionServerPort; //unsigned short
        private final short connectionServerPingPort; //unsigned short
        private final int populationOnline; //must be signed, -1 is a legitimate value meaning not available (for security reason)
        private final PopulationStatus populationStatus;
        private final int maxCharactersPerAccount;
        private final int timeZone;
        private final Status status;
        private final boolean dontRecommend;
        private final int onlinePlayerLimit;
        private final int onlineFreeTrialLimit;

        public ClusterData(final ByteBuffer buffer) {
            clusterId = buffer.getInt();
            connectionServerAddress = BufferUtil.getAscii(buffer);
            connectionServerPort = buffer.getShort();
            connectionServerPingPort = buffer.getShort();
            populationOnline = buffer.getInt();
            populationStatus = PopulationStatus.from(buffer.getInt());
            maxCharactersPerAccount = buffer.getInt();
            timeZone = buffer.getInt();
            status = Status.from(buffer.getInt());
            dontRecommend = BufferUtil.getBoolean(buffer);
            onlinePlayerLimit = buffer.getInt();
            onlineFreeTrialLimit = buffer.getInt();
        }

        @Override
        public void writeToBuffer(final ByteBuffer buffer) {
            BufferUtil.put(buffer, clusterId);
            BufferUtil.put(buffer, connectionServerAddress);
            BufferUtil.put(buffer, connectionServerPort);
            BufferUtil.put(buffer, connectionServerPingPort);
            BufferUtil.put(buffer, populationOnline);
            BufferUtil.put(buffer, populationStatus.value);
            BufferUtil.put(buffer, maxCharactersPerAccount);
            BufferUtil.put(buffer, timeZone);
            BufferUtil.put(buffer, status.value);
            BufferUtil.put(buffer, dontRecommend);
            BufferUtil.put(buffer, onlinePlayerLimit);
            BufferUtil.put(buffer, onlineFreeTrialLimit);
        }

        @Override
        public int compareTo(@NonNull ClusterData o) {
            return Integer.compare(clusterId, o.clusterId);
        }

        public enum Status {
            DOWN(0),
            LOADING(1),
            UP(2),
            LOCKED(3),
            RESTRICTED(4),
            FULL(5);

            private static Status[] values = values();
            private final int value;

            Status(final int value) {
                this.value = value;
            }

            public static Status from(final int value) {
                return values[value];
            }
        }

        public enum PopulationStatus {
            VERY_LIGHT(0),
            LIGHT(1),
            MEDIUM(2),
            HEAVY(3),
            VERY_HEAVY(4),
            EXTREMELY_HEAVY(5),
            FULL(6);

            private static PopulationStatus[] values = values();
            private final int value;

            PopulationStatus(final int value) {
                this.value = value;
            }

            public static PopulationStatus from(final int value) {
                return values[value];
            }
        }
    }
}
