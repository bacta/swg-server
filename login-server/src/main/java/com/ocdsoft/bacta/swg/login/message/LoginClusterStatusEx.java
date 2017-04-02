package com.ocdsoft.bacta.swg.login.message;

import com.ocdsoft.bacta.engine.utils.BufferUtil;
import com.ocdsoft.bacta.swg.protocol.message.GameNetworkMessage;
import com.ocdsoft.bacta.swg.protocol.message.Priority;
import com.ocdsoft.bacta.swg.shared.object.ExtendedClusterData;
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

    private final Set<ExtendedClusterData> extendedClusterDataSet;

    public LoginClusterStatusEx(final ByteBuffer buffer) {
        extendedClusterDataSet = BufferUtil.getTreeSet(buffer, ExtendedClusterData::new);
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        BufferUtil.put(buffer, extendedClusterDataSet);
    }
}
