package com.ocdsoft.bacta.swg.login.message;

import com.ocdsoft.bacta.engine.buffer.BufferUtil;
import com.ocdsoft.bacta.soe.network.message.GameNetworkMessage;
import com.ocdsoft.bacta.soe.network.message.Priority;
import com.ocdsoft.bacta.swg.login.object.ClusterDataExtended;
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
public class LoginClusterStatusEx extends GameNetworkMessage {

    private final Set<ClusterDataExtended> extendedClusterDataSet;

    public LoginClusterStatusEx(final ByteBuffer buffer) {
        extendedClusterDataSet = BufferUtil.getTreeSet(buffer, ClusterDataExtended::new);
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        BufferUtil.put(buffer, extendedClusterDataSet);
    }
}
