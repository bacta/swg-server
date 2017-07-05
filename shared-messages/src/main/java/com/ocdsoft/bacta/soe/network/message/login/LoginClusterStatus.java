package com.ocdsoft.bacta.soe.network.message.login;

import com.ocdsoft.bacta.engine.buffer.BufferUtil;
import com.ocdsoft.bacta.engine.buffer.ByteBufferWritable;
import com.ocdsoft.bacta.soe.network.message.GameNetworkMessage;
import com.ocdsoft.bacta.soe.network.message.game.Priority;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;
import java.util.Set;
import java.util.TreeSet;

@Priority(0x3)
public final class LoginClusterStatus extends GameNetworkMessage {

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

        for (final ClusterData serverEntry : clusterDataSet) {
            serverEntry.writeToBuffer(buffer);
        }
    }

    @Getter
    @AllArgsConstructor
    public static final class ClusterData implements ByteBufferWritable {
	    private final int clusterId;
	    private final String connectionServerAddress;
	    private final int connectionServerPort; //unsigned short
	    private final int connectionServerPingPort; //unsigned short
	    private final int populationOnline; //must be signed, -1 is a legitimate value meaning not available (for security reason)
        private final PopulationStatus populationStatus;
        private final int maxCharactersPerAccount;
        private final int timeZone;
        private final Status status;
        private final boolean dontRecommend;
        private final int onlinePlayerLimit;
        private final int onlineFreeTrialLimit;

        public ClusterData(final ByteBuffer buffer) {
            clusterId = buffer.getInt();
            connectionServerAddress = BufferUtil.getAscii(buffer);
            connectionServerPort = buffer.getShort();
            connectionServerPingPort = buffer.getShort();
            populationOnline = buffer.getInt();
            populationStatus = PopulationStatus.from(buffer.getInt());
            maxCharactersPerAccount = buffer.getInt();
            timeZone = buffer.getInt();
            status = Status.from(buffer.getInt());
            dontRecommend = BufferUtil.getBoolean(buffer);
            onlinePlayerLimit = buffer.getInt();
            onlineFreeTrialLimit = buffer.getInt();
        }

        @Override
        public void writeToBuffer(final ByteBuffer buffer) {
            BufferUtil.put(buffer, clusterId);
            BufferUtil.put(buffer, connectionServerAddress);
            BufferUtil.put(buffer, connectionServerPort);
            BufferUtil.put(buffer, connectionServerPingPort);
            BufferUtil.put(buffer, populationOnline);
            BufferUtil.put(buffer, populationStatus.value);
            BufferUtil.put(buffer, maxCharactersPerAccount);
            BufferUtil.put(buffer, timeZone);
            BufferUtil.put(buffer, status.value);
            BufferUtil.put(buffer, dontRecommend);
            BufferUtil.put(buffer, onlinePlayerLimit);
            BufferUtil.put(buffer, onlineFreeTrialLimit);
        }

        public enum Status {
	        DOWN(0),
            LOADING(1),
            UP(2),
            LOCKED(3),
            RESTRICTED(4),
            FULL(5);

	        private static Status[] values = values();
	        private final int value;

	        Status(final int value) {
	            this.value = value;
            }

            public static Status from(final int value) {
	            return values[value];
            }
        }

        public enum PopulationStatus {
            VERY_LIGHT(0),
            LIGHT(1),
            MEDIUM(2),
            HEAVY(3),
            VERY_HEAVY(4),
            EXTREMELY_HEAVY(5),
            FULL(6);

            private static PopulationStatus[] values = values();
            private final int value;

            PopulationStatus(final int value) {
                this.value = value;
            }

            public static PopulationStatus from(final int value) {
                return values[value];
            }
        }
    }
}
