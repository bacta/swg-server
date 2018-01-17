package io.bacta.login.server;

import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.inject.Inject;
import java.nio.ByteBuffer;

@Getter
@Priority(0x02)
@AllArgsConstructor
public final class ImplicitConnectionTestMessage extends GameNetworkMessage {

    @Inject
    public ImplicitConnectionTestMessage(final ByteBuffer buffer) {

    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {

    }
}
