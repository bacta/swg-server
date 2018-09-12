package io.bacta.game.message.connection;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;


/**
 struct __cppobj ClientPermissionsMessage : GameNetworkMessage
 {
     Archive::AutoVariable<bool> m_canLogin;
     Archive::AutoVariable<bool> m_canCreateRegularCharacter;
     Archive::AutoVariable<bool> m_canCreateJediCharacter;
     Archive::AutoVariable<bool> m_canSkipTutorial;
 }; 
 */

@Getter
@AllArgsConstructor
@Priority(0x4)
public class ClientPermissionsMessage extends GameNetworkMessage {

    private final boolean canLogin;
    private final boolean canCreateRegularCharacter;
    private final boolean canCreateJediCharacter;
    private final boolean canSkipTutorial;

    public ClientPermissionsMessage(ByteBuffer buffer) {

        this.canLogin = BufferUtil.getBoolean(buffer);
        this.canCreateRegularCharacter = BufferUtil.getBoolean(buffer);
        this.canCreateJediCharacter = BufferUtil.getBoolean(buffer);
        this.canSkipTutorial = BufferUtil.getBoolean(buffer);
    }
    
    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        BufferUtil.put(buffer, canLogin);
        BufferUtil.put(buffer, canCreateRegularCharacter);
        BufferUtil.put(buffer, canCreateJediCharacter);
        BufferUtil.put(buffer, canSkipTutorial);
    }
}
