package io.bacta.game.message.object;

import io.bacta.game.MessageId;
import io.bacta.game.ObjControllerMessage;

import java.nio.ByteBuffer;

@MessageId(0x116)
public class CommandQueueEnqueue extends ObjControllerMessage {

    public CommandQueueEnqueue(final ByteBuffer buffer) {
        super(buffer);
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
    
    }
    /**
         23 00 00 00 16 01 00 00 40 42 0F 00 00 00 00 00
    00 00 00 00 00 00 00 00 45 B1 2A 39 00 00 00 00
    00 00 00 00 00 00 00 00 

     */
}
