package com.ocdsoft.bacta.swg.login.message;

import com.ocdsoft.bacta.soe.network.message.GameNetworkMessage;
import com.ocdsoft.bacta.soe.network.message.game.Priority;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

@Priority(0x2)
@Getter
@AllArgsConstructor
public class RequestExtendedClusterInfo extends GameNetworkMessage {

    public RequestExtendedClusterInfo(final ByteBuffer buffer) {

    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {

    }
    /**
         02 00 05 ED 33 8E 00 00 00 00 

     */
}
