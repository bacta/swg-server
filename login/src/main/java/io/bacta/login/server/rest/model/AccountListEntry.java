package io.bacta.login.server.rest.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;

@Getter
@RequiredArgsConstructor
public final class AccountListEntry {
    public final int id;
    public final String username;
    public final ZonedDateTime created;
}
