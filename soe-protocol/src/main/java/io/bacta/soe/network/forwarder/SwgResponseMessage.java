package io.bacta.soe.network.forwarder;

import io.bacta.shared.GameNetworkMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.net.InetSocketAddress;

@AllArgsConstructor
@Getter
public class SwgResponseMessage {
    private final GameNetworkMessage message;
    private final InetSocketAddress remoteSender;
}
