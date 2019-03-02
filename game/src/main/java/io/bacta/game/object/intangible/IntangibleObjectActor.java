package io.bacta.game.object.intangible;

import io.bacta.game.object.ServerObject;
import io.bacta.game.object.ServerObjectActor;

public class IntangibleObjectActor<T extends ServerObject> extends ServerObjectActor<T> {
    protected IntangibleObjectActor(T serverObject) {
        super(serverObject);
    }
}
