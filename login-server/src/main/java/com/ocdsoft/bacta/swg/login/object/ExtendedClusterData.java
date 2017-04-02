package com.ocdsoft.bacta.swg.login.object;

import com.ocdsoft.bacta.engine.buffer.ByteBufferWritable;
import com.ocdsoft.bacta.engine.utils.BufferUtil;
import com.ocdsoft.bacta.swg.server.game.GameServerState;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
 * Created by kyle on 6/4/2016.
 */
@Getter
@AllArgsConstructor
public class ExtendedClusterData implements ByteBufferWritable {

    private final int clusterId;
    private final String branch;
    private final String networkVersion;
    private final int version;
    private final int reserved1;
    private final int reserved2;
    private final int reserved3;
    private final int reserved4;

    ExtendedClusterData(final GameServerState gameServerState) {
        this.clusterId = gameServerState.getClusterId();
        this.branch = gameServerState.getBranch();
        this.networkVersion = gameServerState.getNetworkVersion();
        this.version = gameServerState.getVersion();
        this.reserved1 = 0;
        this.reserved2 = 0;
        this.reserved3 = 0;
        this.reserved4 = 0;
    }

    ExtendedClusterData(final int id) {
        this.clusterId = id;
        this.branch = "";
        this.networkVersion = "";
        this.version = -1;
        this.reserved1 = 0;
        this.reserved2 = 0;
        this.reserved3 = 0;
        this.reserved4 = 0;
    }

    public ExtendedClusterData(ByteBuffer buffer) {
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
        buffer.putInt(clusterId);
        BufferUtil.putAscii(buffer, branch);
        BufferUtil.putAscii(buffer, networkVersion);
        buffer.putInt(version);
        buffer.putInt(reserved1);
        buffer.putInt(reserved2);
        buffer.putInt(reserved3);
        buffer.putInt(reserved4);
    }
}
