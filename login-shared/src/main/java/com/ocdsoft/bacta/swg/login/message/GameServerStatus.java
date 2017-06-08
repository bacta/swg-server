package com.ocdsoft.bacta.swg.login.message;

import com.ocdsoft.bacta.soe.protocol.network.message.GameNetworkMessage;
import com.ocdsoft.bacta.soe.protocol.network.message.Priority;
import com.ocdsoft.bacta.soe.protocol.network.message.Subscribable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

@AllArgsConstructor
@Getter
@Priority(0x4)
public final class GameServerStatus extends GameNetworkMessage implements Subscribable {


    public GameServerStatus(final ByteBuffer buffer) {
        //this.clusterServer = new ClusterData(buffer);
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        //clusterServer.writeToBuffer(buffer);
    }

}
