package io.bacta.login.server.service;

import io.bacta.soe.network.connection.SoeConnection;

public interface ClientService {
    void validateClient(SoeConnection connection, String clientVersion, String id, String key);

    void clientValidated(SoeConnection connection, int bactaId, String username, String sessionKey, boolean isSecure, int gameBits, int subscriptionBits);
}
