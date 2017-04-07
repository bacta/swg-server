package com.ocdsoft.bacta.swg.login.object;

import com.ocdsoft.bacta.engine.buffer.ByteBufferWritable;
import com.ocdsoft.bacta.engine.conf.BactaConfiguration;
import com.ocdsoft.bacta.engine.network.ServerStatus;
import com.ocdsoft.bacta.engine.utils.BufferUtil;
import com.ocdsoft.bacta.engine.utils.UnsignedUtil;
import com.ocdsoft.bacta.soe.protocol.network.io.udp.GameNetworkConfiguration;
import com.ocdsoft.bacta.soe.protocol.util.SoeMessageUtil;
import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;

/**
 *
 * // NGE Struct
 struct LoginClusterStatus_ClusterData
 {
 unsigned int m_clusterId;
 std::string m_connectionServerAddress;
 unsigned __int16 m_connectionServerPort;
 unsigned __int16 m_connectionServerPingPort;
 int m_populationOnline;
 LoginClusterStatus_ClusterData::PopulationStatus m_populationOnlineStatus;
 int m_maxCharactersPerAccount;
 int m_timeZone;
 LoginClusterStatus_ClusterData::Status m_status;
 bool m_dontRecommend;
 unsigned int m_onlinePlayerLimit;
 unsigned int m_onlineFreeTrialLimit;
 }
 *
 */

@Getter
public class ClusterServerEntry implements ByteBufferWritable, Comparable<ClusterServerEntry>  {

    private int id;
    private String connectionServerAddress;
    private int connectionServerPort;
    private int connectionServerPingPort;
    @Setter
    private int populationOnline;
    @Setter
    private PopulationStatus populationOnlineStatus;
    private int maxCharactersPerAccount;
    private int timeZone;
    @Setter
    private ServerStatus status; //enum
    private boolean dontRecommend;
    @Setter
    private int onlinePlayerLimit;
    @Setter
    private int onlineFreeTrialLimit;

    public ClusterServerEntry(final ByteBuffer buffer) {
        id = buffer.getInt();
        connectionServerAddress = BufferUtil.getAscii(buffer);
        connectionServerPort = UnsignedUtil.getUnsignedShort(buffer);
        connectionServerPingPort = UnsignedUtil.getUnsignedShort(buffer);
        populationOnline = buffer.getInt();
        populationOnlineStatus = PopulationStatus.values()[buffer.getInt()];
        maxCharactersPerAccount = buffer.getInt();
        timeZone = buffer.getInt();
        status = ServerStatus.values()[buffer.getInt()];
        dontRecommend = BufferUtil.getBoolean(buffer);
        onlinePlayerLimit = buffer.getInt();
        onlineFreeTrialLimit = buffer.getInt();
    }

    public ClusterServerEntry(final int id) {
        this.id = id;
        connectionServerAddress = "";
        connectionServerPort = 0;
        connectionServerPingPort = 0;
        populationOnline = 0;
        populationOnlineStatus = PopulationStatus.PS_very_light;
        maxCharactersPerAccount = 0;
        timeZone = 0;
        status = ServerStatus.DOWN;
        dontRecommend = true;
        onlinePlayerLimit = 0;
        onlineFreeTrialLimit = 0;
    }

    public ClusterServerEntry(final BactaConfiguration configuration, final GameNetworkConfiguration networkConfiguration) {
        id = networkConfiguration.getClusterId();
        connectionServerAddress = networkConfiguration.getPublicAddress().getHostAddress();
        connectionServerPort = networkConfiguration.getUdpPort();
        connectionServerPingPort = configuration.getInt("Bacta/GameServer", "PingPort");
        populationOnline = 0;
        populationOnlineStatus = PopulationStatus.PS_very_light;
        maxCharactersPerAccount = configuration.getInt("Bacta/GameServer", "MaxCharsPerAccount");
        timeZone = SoeMessageUtil.getTimeZoneValue();
        status = ServerStatus.DOWN;
        dontRecommend = configuration.getBoolean("Bacta/GameServer", "DontRecommended");
        onlinePlayerLimit = configuration.getInt("Bacta/GameServer", "OnlinePlayerLimit");
        onlineFreeTrialLimit = configuration.getInt("Bacta/GameServer", "OnlineFreeTrialLimit");
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        buffer.putInt(id);
        BufferUtil.putAscii(buffer, connectionServerAddress);
        buffer.putShort((short)connectionServerPort);
        buffer.putShort((short)connectionServerPingPort);
        buffer.putInt(populationOnline);
        populationOnlineStatus.writeToBuffer(buffer);
        buffer.putInt(maxCharactersPerAccount);
        buffer.putInt(timeZone);
        status.writeToBuffer(buffer);
        BufferUtil.put(buffer, dontRecommend);
        buffer.putInt(onlinePlayerLimit);
        buffer.putInt(onlineFreeTrialLimit);
    }

    public boolean isDown() { return status == ServerStatus.DOWN; }
    public boolean isLoading() { return status == ServerStatus.LOADING; }
    public boolean isUp()  { return status == ServerStatus.UP;  }
    public boolean isLocked()  { return status == ServerStatus.LOCKED;  }
    public boolean isRestricted()  { return status == ServerStatus.RESTRICTED;  }
    public boolean isFull()  { return status == ServerStatus.FULL;  }
    public boolean isRecommended()  { return !dontRecommend;  }

    @Override
    public int compareTo(ClusterServerEntry o) {
        return connectionServerAddress.compareTo(o.connectionServerAddress);
    }
}
