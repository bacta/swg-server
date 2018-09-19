package io.bacta.game.message;

import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.ByteBuffer;

@Getter
@Priority(0x02)
@RequiredArgsConstructor
public final class GcwRegionsReq extends GameNetworkMessage {
    public GcwRegionsReq(ByteBuffer buffer) {
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {

    }
}
