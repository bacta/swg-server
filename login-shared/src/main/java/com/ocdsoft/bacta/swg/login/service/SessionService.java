package com.ocdsoft.bacta.swg.login.service;

import com.ocdsoft.bacta.swg.login.object.Session;

/**
 * Created by crush on 6/8/2017.
 *
 * Manages active sessions.
 *
 * In the future, we may split this out to its own server: SessionServer, and provide
 * LoginServer a client for validating an identity with SessionServer, or gaining a key from SessionServer if
 * the LoginServer is set to allow client logins.
 *
 * This would be an essential step in allowing Launchpad based, secure logins.
 */
public interface SessionService {
    /**
     * Gets a session based on session key.
     * @param sessionKey The key identifying the session.
     * @return The session corresponding to the key, or null if it doesn't exist.
     */
    Session get(String sessionKey);
    /**
     * Creates a new session for the given account which will expire in the given number of milliseconds in the future
     * from the current time and date.
     * @param accountId The id of the account to which the session belongs.
     * @param expirationMilliseconds The number of milliseconds from the current date at which the session will expire.
     * @return A new session bound to the provided account.
     */
    Session create(int accountId, int expirationMilliseconds);
    /**
     * Check if a session is valid, and not expired.
     * @param sessionKey The key for the session which is being validated.
     * @return True if the session key exists and is not expired. Otherwise, false.
     */
    boolean validate(String sessionKey);
    /**
     * Immediately expires a session.
     * @param sessionKey The key for the session which should be immediately expired.
     */
    void expire(String sessionKey);

    /**
     * Immediately expires a session.
     * @param session The session which should be immediately expired.
     */
    void expire(Session session);
}
