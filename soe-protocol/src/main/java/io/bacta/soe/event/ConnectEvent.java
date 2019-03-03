package io.bacta.soe.event;

import akka.actor.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ConnectEvent implements BactaEvent {
    private ActorRef client;
}
