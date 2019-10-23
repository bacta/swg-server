package io.bacta.game.db;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class NetworkIdBlock {
    private final long startingId;
    private final long finalId;
}
