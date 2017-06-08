package com.ocdsoft.bacta.swg.login.service;

import com.ocdsoft.bacta.engine.buffer.BufferUtil;
import com.ocdsoft.bacta.soe.protocol.network.message.GameNetworkMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
 * Created by kyle on 5/21/2016.
 */
@AllArgsConstructor
@Getter
public class ClusterRegistrationFailed extends GameNetworkMessage {

    public static final String NO_DYNAMIC_REGISTRATION = "Dynamic Registration is not enabled";

    private final String reason;

    public ClusterRegistrationFailed(final ByteBuffer buffer) {
        this.reason = BufferUtil.getAscii(buffer);
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        BufferUtil.putAscii(buffer, reason);
    }
}
