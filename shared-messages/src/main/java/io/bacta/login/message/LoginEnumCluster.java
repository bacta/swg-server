package io.bacta.login.message;

import com.ocdsoft.bacta.engine.buffer.ByteBufferWritable;
import com.ocdsoft.bacta.engine.buffer.BufferUtil;
import io.bacta.shared.GameNetworkMessage;
import io.bacta.game.Priority;
import lombok.Getter;

import java.nio.ByteBuffer;
import java.util.Set;
import java.util.TreeSet;

@Priority(0x2)
@Getter
public class LoginEnumCluster extends GameNetworkMessage {

    private final Set<LoginEnumCluster.Data> clusterDataSet;
    private int maxCharactersPerAccount;

    public LoginEnumCluster() {
        clusterDataSet = new TreeSet<>();
        maxCharactersPerAccount = 2;
    }

	public LoginEnumCluster(final Set<LoginEnumCluster.Data> clusterServerSet, final int maxCharactersPerAccount) {
        this.clusterDataSet = clusterServerSet;
        this.maxCharactersPerAccount = maxCharactersPerAccount;
	}

    public LoginEnumCluster(final ByteBuffer buffer) {
        clusterDataSet = BufferUtil.getTreeSet(buffer, Data::new);
        maxCharactersPerAccount = buffer.getInt();
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        BufferUtil.put(buffer, clusterDataSet);
        buffer.putInt(maxCharactersPerAccount);
    }

    @Getter
    public static class Data implements ByteBufferWritable, Comparable<Data> {

        private final int id;
        private final String name;
        private final int timezone;  // Offset from GMT in seconds

        public Data(final int id, final String name, final int timezone) {
            this.id = id;
            this.name = name;
            this.timezone = timezone;
        }

        public Data(final ByteBuffer buffer) {
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
        public int compareTo(Data o) {
            return Integer.compare(id, o.getId());
        }
    }
}
