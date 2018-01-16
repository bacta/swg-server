package io.bacta.login.server.session;

public final class SessionExpiredException extends SessionException {
    public SessionExpiredException() {
        super("The session expired.");
    }
}
