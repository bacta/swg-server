package io.bacta.game.object.universe.group;

import io.bacta.game.object.universe.UniverseObjectActor;

public class GroupObjectActor extends UniverseObjectActor<GroupObject> {
    protected GroupObjectActor(GroupObject serverObject) {
        super(serverObject);
    }

    protected GroupObjectActor(long objectId) {
        super(objectId);
    }
}
