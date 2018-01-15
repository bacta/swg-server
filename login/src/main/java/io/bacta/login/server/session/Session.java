package io.bacta.login.server.session;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public final class Session {
    private static final long SESSION_TIME_TO_LIVE = 1000 * 60 * 60; //1 hour.

    private final String key;
    private final int accountId;
    private final long created = System.currentTimeMillis();

    public boolean isExpired() {
        final long now = System.currentTimeMillis();
        return now > created + SESSION_TIME_TO_LIVE;
    }
}
