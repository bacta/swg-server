package bacta.io.session.client;

/**
 * Created by crush on 7/4/2017.
 */
public interface SessionClient {
    /**
     * Validate an existing session key.
     * @param key The key being validated.
     * @return A session result which can be interrogated about the success or failure of the operation.
     */
    SessionResult validate(String key);

    /**
     * Login with a username and password, obtaining a session key from the session server.
     * @param username The username.
     * @param password The password.
     * @return A session result with the status of the operation, and the session key if the operation succeeded.
     */
    SessionResult login(String username, String password);
}