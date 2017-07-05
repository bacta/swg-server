package io.bacta.login.message;

import io.bacta.shared.GameNetworkMessage;
import io.bacta.game.Priority;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

@AllArgsConstructor
@Getter
@Priority(0x4)
public final class GameServerStatus extends GameNetworkMessage {


    public GameServerStatus(final ByteBuffer buffer) {
        //this.clusterServer = new ClusterData(buffer);
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        //clusterServer.writeToBuffer(buffer);
    }

}
