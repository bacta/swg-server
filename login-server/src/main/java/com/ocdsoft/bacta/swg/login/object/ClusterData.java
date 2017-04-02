package com.ocdsoft.bacta.swg.login.object;

import com.google.inject.Inject;
import com.ocdsoft.bacta.engine.buffer.ByteBufferWritable;
import com.ocdsoft.bacta.engine.conf.BactaConfiguration;
import com.ocdsoft.bacta.engine.utils.BufferUtil;
import com.ocdsoft.bacta.soe.io.udp.GameNetworkConfiguration;
import com.ocdsoft.bacta.soe.util.SoeMessageUtil;
import com.ocdsoft.bacta.swg.server.game.GameServerState;
import com.ocdsoft.bacta.swg.server.login.message.LoginClusterStatus;
import com.ocdsoft.bacta.swg.server.login.message.LoginEnumCluster;
import lombok.Getter;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

@Getter
public class ClusterData implements ByteBufferWritable, Comparable<ClusterData> {

    private final int id;
    private final InetSocketAddress remoteAddress;
    private final int tcpPort;
    private final String serverKey;
    private final String name;
    private final LoginClusterStatus.ClusterData statusClusterData;
    private final LoginEnumCluster.ClusterData clusterData;
    private final ExtendedClusterData extendedClusterData;

    @Inject
    public ClusterData(final BactaConfiguration configuration,
                       final GameNetworkConfiguration networkConfiguration,
                       final GameServerState gameServerState) {

        id = networkConfiguration.getClusterId();
        remoteAddress = new InetSocketAddress(
                networkConfiguration.getPublicAddress(),
                networkConfiguration.getUdpPort()
                );
        tcpPort = configuration.getInt("Bacta/GameServer", "TcpPort");
        serverKey = configuration.getString("Bacta/GameServer", "ServerKey");
        name = configuration.getString("Bacta/GameServer", "ServerName");

        statusClusterData = new LoginClusterStatus.ClusterData(configuration, networkConfiguration);
        clusterData = new LoginEnumCluster.ClusterData(id, name);

        extendedClusterData = new ExtendedClusterData(gameServerState);
    }

    public ClusterData(final int id, final InetSocketAddress remoteAddress, final int tcpPort, final String serverKey, final String name) {
        this.id = id;
        this.remoteAddress = remoteAddress;
        this.tcpPort = tcpPort;
        this.serverKey = serverKey;
        this.name = name;

        statusClusterData = new LoginClusterStatus.ClusterData(id);
        clusterData = new LoginEnumCluster.ClusterData(id, name);
        extendedClusterData = new ExtendedClusterData(id);
    }

    public ClusterData(ByteBuffer buffer) {
        id = buffer.getInt();
        remoteAddress = new InetSocketAddress(
                BufferUtil.getAscii(buffer),
                buffer.getInt()
        );
        tcpPort = buffer.getInt();
        serverKey = BufferUtil.getAscii(buffer);
        name = BufferUtil.getAscii(buffer);

        statusClusterData = new LoginClusterStatus.ClusterData(buffer);
        clusterData = new LoginEnumCluster.ClusterData(buffer);
        extendedClusterData = new ExtendedClusterData(buffer);
    }

    @Override
    public int compareTo(ClusterData o) {
        return o.getName().compareTo(getName());
    }


    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        buffer.putInt(id);
        BufferUtil.putAscii(buffer, remoteAddress.getHostString());
        buffer.putInt(remoteAddress.getPort());
        buffer.putInt(tcpPort);
        BufferUtil.putAscii(buffer, serverKey);
        BufferUtil.putAscii(buffer, name);

        statusClusterData.writeToBuffer(buffer);
        clusterData.writeToBuffer(buffer);
        extendedClusterData.writeToBuffer(buffer);
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        ClusterData that = (ClusterData) o;

        return id == that.id && serverKey.equals(that.serverKey);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (serverKey != null ? serverKey.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (clusterData != null ? clusterData.hashCode() : 0);
        result = 31 * result + (clusterData != null ? clusterData.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ClusterEntry{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status='" + statusClusterData.getStatus() + '\'' +
                ", timezone='" + SoeMessageUtil.getTimeZoneOffsetFromValue(clusterData.getTimezone()) + '\'' +
                '}';
    }

}
