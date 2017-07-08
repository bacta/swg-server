package io.bacta.soe.service;

import io.bacta.soe.network.connection.SoeConnectionProvider;
import io.bacta.soe.network.connection.SoeUdpConnection;
import io.bacta.soe.network.connection.SoeUdpConnectionCache;

/**
 * Created by kyle on 7/7/2017.
 */
public interface InternalMessageService {
    void setConnectionObjects(SoeUdpConnectionCache connectionCache, SoeConnectionProvider connectionProvider);
    SoeUdpConnection getSessionServerConnection();
    SoeUdpConnection getGalaxyServerConnection();
    SoeUdpConnection getConnectionServerConnection();
    SoeUdpConnection getLoginServerConnection();
}
