package com.ocdsoft.bacta.soe.network.message.login;

import com.ocdsoft.bacta.engine.buffer.BufferUtil;
import com.ocdsoft.bacta.soe.network.message.GameNetworkMessage;
import com.ocdsoft.bacta.soe.network.message.game.Priority;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
 * Created by crush on 7/4/2017.
 *
 * LoginServer to GalaxyServer. Tells the GalaxyServer its ClusterId according to the LoginServer. This message also
 * tells the GalaxyServer that the LoginServer has recognized it as a legitimate GalaxyServer in its serviceable network.
 */
@Getter
@Priority(0x02)
@AllArgsConstructor
public final class ClusterId extends GameNetworkMessage {
    private final int clusterId;

    public ClusterId(final ByteBuffer buffer) {
        clusterId = buffer.getInt();
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        BufferUtil.put(buffer, clusterId);
    }
}
