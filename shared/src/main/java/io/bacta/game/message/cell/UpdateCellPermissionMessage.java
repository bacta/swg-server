package io.bacta.game.message.cell;

import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.Getter;

import java.nio.ByteBuffer;

/**


 SOECRC32.hashCode(UpdateCellPermissionMessage.class.getSimpleName()); // 0xf612499c
 */
@Getter
@Priority(0x1)
public class UpdateCellPermissionMessage extends GameNetworkMessage {

    public UpdateCellPermissionMessage() {

    }

    public UpdateCellPermissionMessage(final ByteBuffer buffer) {

    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {

    }
}
