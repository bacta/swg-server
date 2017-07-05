package com.ocdsoft.bacta.soe.network.message.galaxy;

import com.ocdsoft.bacta.engine.buffer.BufferUtil;
import com.ocdsoft.bacta.soe.network.message.GameNetworkMessage;
import com.ocdsoft.bacta.soe.network.message.game.Priority;
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
