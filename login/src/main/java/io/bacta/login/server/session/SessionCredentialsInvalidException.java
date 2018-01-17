package io.bacta.login.server.session;

public final class SessionCredentialsInvalidException extends SessionException {
    public SessionCredentialsInvalidException() {
        super(String.format("The credentials provided were invalid and a session could not be established."));
    }
}
