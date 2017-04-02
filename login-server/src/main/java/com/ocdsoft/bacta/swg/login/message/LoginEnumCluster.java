package com.ocdsoft.bacta.swg.login.message;

import com.google.inject.Inject;
import com.ocdsoft.bacta.engine.buffer.ByteBufferWritable;
import com.ocdsoft.bacta.engine.utils.BufferUtil;
import com.ocdsoft.bacta.swg.protocol.message.GameNetworkMessage;
import com.ocdsoft.bacta.swg.protocol.message.Priority;
import lombok.Getter;
import org.joda.time.DateTimeZone;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Priority(0x2)
@Getter
public class LoginEnumCluster extends GameNetworkMessage {

    private final Set<ClusterData> clusterDataSet;
    private int maxCharactersPerAccount;

    @Inject
    public LoginEnumCluster() {
        clusterDataSet = new TreeSet<>();
    }

	public LoginEnumCluster(final Collection<com.ocdsoft.bacta.swg.shared.object.ClusterData> clusterServerSet, final int maxCharactersPerAccount) {
        this();
        clusterDataSet.addAll(clusterServerSet.stream()
                .map(com.ocdsoft.bacta.swg.shared.object.ClusterData::getClusterData)
                .collect(Collectors.toList())
        );
        this.maxCharactersPerAccount = maxCharactersPerAccount;
	}

    public LoginEnumCluster(final ByteBuffer buffer) {
        clusterDataSet = BufferUtil.getTreeSet(buffer, LoginEnumCluster.ClusterData::new);
        maxCharactersPerAccount = buffer.getInt();
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        BufferUtil.put(buffer, clusterDataSet);
        buffer.putInt(maxCharactersPerAccount);
    }

    @Getter
    public static class ClusterData implements ByteBufferWritable, Comparable<ClusterData> {

        private final int id;
        private final String name;
        private final int timezone;  // Offset from GMT in seconds

        public ClusterData(final int id, final String name) {
            this.id = id;
            this.name = name;
            this.timezone = DateTimeZone.getDefault().getOffset(null) / 1000;
        }

        public ClusterData(final ByteBuffer buffer) {
            id = buffer.getInt();
            name = BufferUtil.getAscii(buffer);
            timezone = buffer.getInt();
        }

        @Override
        public void writeToBuffer(final ByteBuffer buffer) {
            buffer.putInt(id);
            BufferUtil.putAscii(buffer, name);
            buffer.putInt(timezone);
        }

        @Override
        public int compareTo(ClusterData o) {
            return Integer.compare(id, o.getId());
        }
    }
}
