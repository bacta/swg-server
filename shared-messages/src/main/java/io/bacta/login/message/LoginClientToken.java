package io.bacta.login.message;

import com.ocdsoft.bacta.engine.buffer.BufferUtil;
import io.bacta.shared.GameNetworkMessage;
import io.bacta.game.Priority;
import lombok.AllArgsConstructor;

import java.nio.ByteBuffer;

@AllArgsConstructor
@Priority(0x4)
public final class LoginClientToken extends GameNetworkMessage {
    private final String authToken;
    private final int bactaId;
    private final String username;

    public LoginClientToken(final ByteBuffer buffer) {
        authToken = BufferUtil.getBinaryString(buffer);
        bactaId = buffer.getInt();
        username = BufferUtil.getAscii(buffer);
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        BufferUtil.putBinaryString(buffer, authToken);
        buffer.putInt(bactaId);
        BufferUtil.putAscii(buffer, username);
        //This extra information shouldn't exist...
        //buffer.put((byte) 0);
        //buffer.putShort((short) 0);
    }

    /**  Example
	 *  04 00 
	 *  C6 96 B2 AA 
	 *  08 00 00 00 
	 *  EF F8 3C 5D 
	 *  66 28 00 00 
	 *  40 D9 52 76 
	 *  04 00 4B 79 6C 65 
	 *  00 
	 *  00 00 
	 */
}
