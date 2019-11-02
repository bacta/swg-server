package io.bacta.soe.network.relay;

import io.bacta.shared.GameNetworkMessage;
import io.bacta.soe.context.SoeSessionContext;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SwgRequestMessage {
    final SoeSessionContext context;
    final GameNetworkMessage gameNetworkMessage;
}
