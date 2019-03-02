package io.bacta.soe.network.forwarder;

import io.bacta.shared.GameNetworkMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.net.InetSocketAddress;

@AllArgsConstructor
@Getter
public class SwgRequestMessage {
    final InetSocketAddress remoteAddress;
    final GameNetworkMessage gameNetworkMessage;
}
