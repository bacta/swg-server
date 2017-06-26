package com.ocdsoft.bacta.soe.protocol.event;

import co.paralleluniverse.actors.Actor;
import com.ocdsoft.bacta.soe.protocol.network.connection.SoeUdpConnection;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by kyle on 6/3/2016.
 */
@AllArgsConstructor
@Getter
public class DisconnectEvent implements Event {
    private Actor
    private final SoeUdpConnection connection;
}
