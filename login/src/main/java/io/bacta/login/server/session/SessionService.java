package io.bacta.login.server.session;

/**
 * Sessions are used to track active accounts and various properties about those users. For example, a session allows us
 * to track active game time for an account. It also allows us to set special features for an account.
 * <p>
 * Once established, a session has a set time to live (TTL). Once that TTL expires, the session is no longer good, and
 * a user who is using that session may have to establish a new session to continue using a service.
 */
public interface SessionService {
    /**
     * Establishes a new session for the account authenticated with the provided credentials.
     *
     * @param username The username identifying the account.
     * @param password The password identifying the account.
     * @return A new session representing the established session.
     * @throws SessionCredentialsInvalidException If the username or password was incorrect.
     */
    Session establish(String username, String password) throws
            SessionCredentialsInvalidException;

    /**
     * Validates a session key to ensure that is is valid and was issued for the given account id.
     *
     * @param sessionKey The key for the session.
     * @param accountId  The id of the account of which the session is supposed to belong.
     * @return Returns the existing session information if validation succeeds.
     * @throws SessionInvalidException If the session is invalid with the provided information.
     * @throws SessionExpiredException If the session is valid but expired.
     */
    Session validate(String sessionKey, int accountId) throws
            SessionInvalidException,
            SessionExpiredException;

    /**
     * Touches the session, extending its time to live on the session server.
     *
     * @param sessionKey The key for the session that is being extended.
     */
    void touch(String sessionKey) throws
            SessionExpiredException;

    /**
     * Terminates a session.
     *
     * @param sessionKey The key identifying the session to terminate.
     */
    void logout(String sessionKey);
}
