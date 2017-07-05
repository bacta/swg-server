package com.ocdsoft.bacta.swg.login.service;

import com.ocdsoft.bacta.soe.network.connection.SoeUdpConnection;

/**
 * Created by crush on 7/3/2017.
 */
public interface SessionService {
    boolean validate(SoeUdpConnection connection, String key);
    boolean login(SoeUdpConnection connection, String username, String password);

    //KeyShareKey getCurrentKey();
    //KeyShareToken makeToken(byte[] data);

    void pushKeys(/* CentralServerConnection* targetCentralServer */);
    void pushKeyToAllServers();
}
