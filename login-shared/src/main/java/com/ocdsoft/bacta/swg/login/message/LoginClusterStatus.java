package com.ocdsoft.bacta.swg.login.message;

import com.ocdsoft.bacta.soe.protocol.network.message.GameNetworkMessage;
import com.ocdsoft.bacta.soe.protocol.network.message.Priority;
import com.ocdsoft.bacta.swg.login.object.ClusterServerEntry;

import java.nio.ByteBuffer;
import java.util.Set;
import java.util.TreeSet;

@Priority(0x3)
public class LoginClusterStatus extends GameNetworkMessage {

    private final Set<ClusterServerEntry> clusterDataSet;

	public LoginClusterStatus(final Set<ClusterServerEntry> clusterServerSet) {
        this.clusterDataSet = clusterServerSet;
	}

    public LoginClusterStatus(final ByteBuffer buffer) {
        clusterDataSet = new TreeSet<>();
        final int count = buffer.getInt();
        for(int i = 0; i < count; ++i) {
            final ClusterServerEntry serverEntry = new ClusterServerEntry(buffer);
            clusterDataSet.add(serverEntry);
        }
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {

        buffer.putInt(clusterDataSet.size());

        for (ClusterServerEntry serverEntry : clusterDataSet) {
            serverEntry.writeToBuffer(buffer);
        }
    }
}
