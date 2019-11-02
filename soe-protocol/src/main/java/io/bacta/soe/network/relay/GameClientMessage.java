package io.bacta.soe.network.relay;

import io.bacta.shared.GameNetworkMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GameClientMessage {
    private final int connectonId;
    private final GameNetworkMessage message;
}
