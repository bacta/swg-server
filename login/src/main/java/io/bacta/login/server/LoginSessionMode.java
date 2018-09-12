package io.bacta.login.server;

public enum LoginSessionMode {
    /**
     * The login server should always try to establish a new session with the credentials provided.
     */
    ESTABLISH,
    /**
     * The login server should always try to use the credentials provided as the session token.
     */
    VALIDATE,
    /**
     * The login server should try to discover if the credentials provided are a valid session token, and if
     * not, then try to use the credentials to establish a new session.
     */
    DISCOVER
}
