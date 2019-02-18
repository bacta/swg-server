package io.bacta.soe.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.net.InetSocketAddress;

@AllArgsConstructor
@Getter
public class DisconnectEvent implements BactaEvent {
    private InetSocketAddress remoteAddress;
}
