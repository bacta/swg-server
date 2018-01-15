package io.bacta.login.message;

import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.ByteBuffer;

/**
 * Communicates that the login server is online and accepting connections. Any galaxy servers that receive this
 * request should respond with the identification process by sending the `GalaxyServerId` and subsequent
 * messages if they are accepted.
 */
@Getter
@Priority(0x02)
@RequiredArgsConstructor
public final class LoginServerOnline extends GameNetworkMessage {

    public LoginServerOnline(final ByteBuffer buffer) {

    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
    }
}
