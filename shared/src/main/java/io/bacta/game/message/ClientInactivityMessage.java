package io.bacta.game.message;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.game.MessageId;
import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.ByteBuffer;

/**
 * The client sends this message when the client has gone inactive for 60 minutes. It also seems to send this
 * if the server doesn't respond to the data transforms.
 * <p>
 * SOE obfuscated the name of this message to 28afefcc187a11dc888b001. Thus, the CRC for this message is the CRC of this
 * obfuscated value rather than that of ClientInactivityMessage.
 */
@Getter
@Priority(0x2)
@MessageId(0x173b91c2)
@RequiredArgsConstructor
public final class ClientInactivityMessage extends GameNetworkMessage {
    private final boolean inactive;

    public ClientInactivityMessage(final ByteBuffer buffer) {
        inactive = BufferUtil.getBoolean(buffer);
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        BufferUtil.put(buffer, inactive);
    }
}