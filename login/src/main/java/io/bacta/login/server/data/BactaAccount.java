package io.bacta.login.server.data;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;

/**
 * Represents an account in the Bacta Network.
 */
@Data
@RequiredArgsConstructor
public final class BactaAccount {
    private final int id;
    private final ZonedDateTime created;
    private final String username;
    private String password;
}
