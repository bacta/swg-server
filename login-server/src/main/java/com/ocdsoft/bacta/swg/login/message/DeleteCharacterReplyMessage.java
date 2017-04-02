package com.ocdsoft.bacta.swg.login.message;

import com.ocdsoft.bacta.swg.protocol.message.GameNetworkMessage;
import com.ocdsoft.bacta.swg.protocol.message.Priority;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
 * Created by kyle on 5/29/2016.
 */
@Getter
@Priority(0x05)
@AllArgsConstructor
public class DeleteCharacterReplyMessage extends GameNetworkMessage {

    private final int status;

    public DeleteCharacterReplyMessage(final ByteBuffer buffer) {
        status = buffer.getInt();
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        buffer.putInt(status);
    }
}