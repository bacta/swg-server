package io.bacta.login.server.session;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public final class SessionToken {
    private final int accountId;
    private final String token;
}
