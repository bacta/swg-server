package io.bacta.login.server.session;

/**
 * Sessions are used by external programs to initiate a login session. For example, the classic SWG launchpad would
 * allow a player to login with HTTPS, obtain a session token, and then pass that token to the SWG client. Now, instead
 * of forcing the player to supply credentials again, the client would automatically send this session token for
 * authentication.
 * <p>
 * EstablishMode is the process of obtaining a session token by providing a username and password. This is the mode the
 * Launchpad would use to obtain the session token as described above. If no session token is passed to the SWG client,
 * it will use EstablishMode to attempt to obtain a session token.
 * <p>
 * ValidationMode is the process of using an already configured session token to authenticate with the login server.
 * <p>
 * DiscoveryMode will attempt to decide which mode to use by inspecting the id and key provided.
 */
public interface SessionService {
    Session establish(String username, String password) throws SessionException;

    Session validate(String sessionKey);
}
