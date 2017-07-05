package com.ocdsoft.bacta.swg.login.service;

import com.ocdsoft.bacta.soe.network.connection.SoeUdpConnection;

/**
 * Created by crush on 7/2/2017.
 */
public interface ClientService {
    /**
     * Attempts to validate a client's connection credentials.
     *
     * @param clientVersion The version of the client that is being connected.
     * @param id            The id identifying the client. This could be a username or a session id.
     * @param key           The key identifying the client. This could be a password or a session key.
     */
    void validateClient(SoeUdpConnection connection, String clientVersion, String id, String key);

    /**
     * Behavior that occurs after a client has been validated.
     *
     * @param connection
     * @param bactaId
     * @param username
     * @param sessionKey
     * @param isSecure
     * @param gameBits
     * @param subscriptionBits
     */
    void clientValidated(SoeUdpConnection connection, int bactaId, String username, String sessionKey, boolean isSecure, int gameBits, int subscriptionBits);
}
