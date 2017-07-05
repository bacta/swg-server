package io.bacta.login.message;

import com.ocdsoft.bacta.engine.buffer.BufferUtil;
import com.ocdsoft.bacta.engine.buffer.ByteBufferWritable;
import io.bacta.game.GameNetworkMessage;
import io.bacta.game.Priority;
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
