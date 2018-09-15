package io.bacta.game.message.object;

import io.bacta.game.MessageId;
import io.bacta.game.ObjControllerMessage;

import java.nio.ByteBuffer;

@MessageId(0x71)
public class DataTransform extends ObjControllerMessage {

    public DataTransform(final ByteBuffer buffer) {
        super(buffer);
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
    
    }
    /**
         21 00 00 00 71 00 00 00 40 42 0F 00 00 00 00 00
    00 00 00 00 42 D1 E9 DD 01 00 00 00 00 00 00 00
    00 00 00 00 00 00 00 00 00 00 80 3F 00 00 00 00
    01 00 43 43 00 00 00 00 00 00 00 00 00 00 00 00
    00 

     */
}
