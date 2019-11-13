package io.bacta.login.server.session;

public interface SessionTokenProvider {
    SessionToken provide(String username, String password) throws SessionException;
}
