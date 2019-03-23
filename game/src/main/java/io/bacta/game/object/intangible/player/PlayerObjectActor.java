package io.bacta.game.object.intangible.player;

import io.bacta.game.object.intangible.IntangibleObjectActor;

public class PlayerObjectActor extends IntangibleObjectActor<PlayerObject> {
    protected PlayerObjectActor(PlayerObject serverObject) {
        super(serverObject);
    }

    protected PlayerObjectActor(long objectId) {
        super(objectId);
    }
}
