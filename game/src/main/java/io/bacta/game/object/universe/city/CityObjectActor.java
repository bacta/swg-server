package io.bacta.game.object.universe.city;

import io.bacta.game.object.universe.UniverseObjectActor;

public class CityObjectActor extends UniverseObjectActor<CityObject> {
    protected CityObjectActor(CityObject serverObject) {
        super(serverObject);
    }

    protected CityObjectActor(long objectId) {
        super(objectId);
    }
}
