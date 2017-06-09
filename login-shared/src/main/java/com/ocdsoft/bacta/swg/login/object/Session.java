package com.ocdsoft.bacta.swg.login.object;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Created by crush on 6/8/2017.
 *
 * Represents a current authentication session.
 */
@Getter
@AllArgsConstructor
public final class Session {
    /**
     * The session key uniquely identifying the session.
     */
    private final String key;
    /**
     * The account to which the session is associated.
     */
    private final int accountId;
    /**
     * The date & time when the session will expire.
     */
    private final LocalDateTime expirationDate;
}
