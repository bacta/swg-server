package io.bacta.game.player;

import akka.actor.ActorRef;
import io.bacta.shared.localization.StringId;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
class PlayerCreationException extends Exception {
    private final ActorRef client;
    private final String characterName;
    private final StringId reason;
}
