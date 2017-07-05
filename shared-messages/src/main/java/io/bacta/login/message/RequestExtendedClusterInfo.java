package io.bacta.login.message;



import io.bacta.shared.GameNetworkMessage;
import io.bacta.game.Priority;

import java.nio.ByteBuffer;

@Priority(0x2)
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
