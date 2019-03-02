package io.bacta.game.object.universe;

import io.bacta.game.object.ServerObjectActor;

public class UniverseObjectActor<T extends UniverseObject> extends ServerObjectActor<T> {
    protected UniverseObjectActor(T serverObject) {
        super(serverObject);
    }
}
