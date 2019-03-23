package io.bacta.game.object.tangible.factory;

import io.bacta.game.object.tangible.TangibleObjectActor;

public class FactoryObjectActor extends TangibleObjectActor<FactoryObject> {
    protected FactoryObjectActor(FactoryObject tangibleObject) {
        super(tangibleObject);
    }

    protected FactoryObjectActor(long objectId) {
        super(objectId);
    }
}
