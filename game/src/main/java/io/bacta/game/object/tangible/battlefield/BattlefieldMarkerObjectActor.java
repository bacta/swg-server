package io.bacta.game.object.tangible.battlefield;

import io.bacta.game.object.tangible.TangibleObjectActor;

public class BattlefieldMarkerObjectActor extends TangibleObjectActor<BattlefieldMarkerObject> {
    protected BattlefieldMarkerObjectActor(BattlefieldMarkerObject tangibleObject) {
        super(tangibleObject);
    }

    protected BattlefieldMarkerObjectActor(long objectId) {
        super(objectId);
    }
}
