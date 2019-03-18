package io.bacta.soe.network.connection;

import io.bacta.engine.network.connection.ConnectionState;

import java.net.InetSocketAddress;

/**
 * Created by kyle on 7/16/2017.
 */
public interface DefaultSoeUdpConnectionMBean {
    InetSocketAddress getRemoteAddress();
    int getId();
    ConnectionState getConnectionState();
    void setConnectionState(ConnectionState state);
    int getProtocolVersion();
    byte getCrcBytes();
    boolean isCompression();
    int getMaxRawPacketSize();
    long getLastActivity();
    long getLastRemoteActivity();
    int getMasterPingTime();
    int getAveragePingTime();
    int getLowPingTime();
    int getHighPingTime();
    int getLastPingTime();
}
