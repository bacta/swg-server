package io.bacta.galaxy.message;

import bacta.io.buffer.BufferUtil;
import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
 * Created by crush on 7/4/2017.
 *
 * This message goes from the GalaxyServer to the LoginServer, telling it which cluster it is servicing.
 *
 * The LoginServer will reply with ClusterId if the GalaxyServer is recognized and registered. Otherwise, it may send
 * nothing in reply if it doesn't recognize the GalaxyServer.
 */
@Getter
@Priority(0x02)
@AllArgsConstructor
public final class LoginClusterName extends GameNetworkMessage {
    private final String clusterName;
    private final int timeZone;

    public LoginClusterName(final ByteBuffer buffer) {
        clusterName = BufferUtil.getAscii(buffer);
        timeZone = buffer.getInt();
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        BufferUtil.put(buffer, clusterName);
        BufferUtil.put(buffer, timeZone);
    }
}
