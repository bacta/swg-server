package io.bacta.login.server.service;

import io.bacta.soe.context.SoeRequestContext;

public interface ClientService {
    /**
     * This method should be called in response to receiving the {@link io.bacta.login.message.LoginClientId} message
     * from an authenticating client. The client could be an SWG client, or some other application that understands
     * the protocol for communicating with the login server.
     * <p>
     * Upon successful validation, the client will be issued a session key which it can then use to authenticate from
     * that point forward.
     *
     * @param context    The incoming connection.
     * @param clientVersion The version of the client. The login server may require this to be one of a handful of versions.
     * @param id            The login id that was used. This may be an account id if a session is being passed as the key. Otherwise,
     *                      it may be the username of the account that is attempting to authenticate.
     * @param key           The login key that was used. This may be the session key that was already obtained, or it may be a user
     *                      account password being used to obtain a session.
     */
    void validateClient(SoeRequestContext context, String clientVersion, String id, String key);

    /**
     * Since client validation is an asynchronous process, this method will be called when it is completed.
     *
     * @param context
     * @param bactaId
     * @param username
     * @param sessionKey
     * @param isSecure
     * @param gameBits
     * @param subscriptionBits
     */
    void clientValidated(SoeRequestContext context, int bactaId, String username, String sessionKey, boolean isSecure, int gameBits, int subscriptionBits);
}
