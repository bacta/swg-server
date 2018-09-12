package io.bacta.game.message.connection;


import io.bacta.engine.buffer.BufferUtil;
import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;


/**
 struct __cppobj ClientIdMsg : GameNetworkMessage
 {
     Archive::AutoVariable<unsigned long> m_gameBitsToClear;
     Archive::AutoArray<unsigned char> token;
     Archive::AutoVariable<std::string > version;
     char *tokenData;
 }; 
 */
@Getter
@Priority(0x4)
@AllArgsConstructor
public class ClientIdMsg extends GameNetworkMessage {

    private final int gameBitsToClear;
    private final String token;
    private final String clientVersion;

    public ClientIdMsg(ByteBuffer buffer) {
        gameBitsToClear = buffer.getInt();
        token = BufferUtil.getBinaryString(buffer);
        clientVersion = BufferUtil.getAscii(buffer);
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        buffer.putInt(gameBitsToClear);
        BufferUtil.putBinaryString(buffer, token);
        BufferUtil.putAscii(buffer, clientVersion);
    }
    /**
         04 00 26 92 89 D5 00 00 00 00 25 00 00 00 31 35
    32 34 31 32 37 38 37 36 37 37 34 30 36 39 35 33
    32 32 36 38 33 35 34 36 32 31 39 36 35 30 36 37
    35 34 33 0E 00 32 30 30 35 31 30 31 30 2D 31 37
    3A 30 30 

     */
}
