package com.ocdsoft.bacta.swg.login.message;

import com.ocdsoft.bacta.soe.network.message.GameNetworkMessage;
import com.ocdsoft.bacta.soe.network.message.Priority;
import com.ocdsoft.bacta.swg.login.object.ClusterData;

import java.nio.ByteBuffer;
import java.util.Set;
import java.util.TreeSet;

@Priority(0x3)
public class LoginClusterStatus extends GameNetworkMessage {

    private final Set<ClusterData> clusterDataSet;

	public LoginClusterStatus(final Set<ClusterData> clusterServerSet) {
        this.clusterDataSet = clusterServerSet;
	}

    public LoginClusterStatus(final ByteBuffer buffer) {
        clusterDataSet = new TreeSet<>();
        final int count = buffer.getInt();
        for(int i = 0; i < count; ++i) {
            final ClusterData serverEntry = new ClusterData(buffer);
            clusterDataSet.add(serverEntry);
        }
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {

        buffer.putInt(clusterDataSet.size());

        for (ClusterData serverEntry : clusterDataSet) {
            serverEntry.writeToBuffer(buffer);
        }
    }
}
