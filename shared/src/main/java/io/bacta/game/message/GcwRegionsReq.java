package io.bacta.game.message;

import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

@Getter
@Priority(0x01)
@AllArgsConstructor
public class GcwRegionsReq extends GameNetworkMessage {

    public GcwRegionsReq(final ByteBuffer buffer) {

    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {

    }
}