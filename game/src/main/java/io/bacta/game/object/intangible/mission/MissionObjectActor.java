package io.bacta.game.object.intangible.mission;

import io.bacta.game.object.intangible.IntangibleObjectActor;

public class MissionObjectActor extends IntangibleObjectActor<MissionObject> {
    protected MissionObjectActor(MissionObject serverObject) {
        super(serverObject);
    }

    protected MissionObjectActor(long objectId) {
        super(objectId);
    }
}
