package io.bacta.login.server.session;

public interface SessionTokenProvider {
    SessionToken Provide(String username, String password);
}
