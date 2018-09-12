package io.bacta.login.server.session;

public final class SessionInvalidException extends SessionException {
    public SessionInvalidException() {
        super("The session is not valid.");
    }
}
