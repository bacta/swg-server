package io.bacta.login.server.service;

import lombok.Getter;

@Getter
public final class InvalidClientException extends Exception {
    private final String clientVersion;

    public InvalidClientException(final String clientVersion) {
        super();

        this.clientVersion = clientVersion;
    }
}
