package io.bacta.game.object;

public class StaticObjectActor extends ServerObjectActor<StaticObject> {
    protected StaticObjectActor(StaticObject serverObject) {
        super(serverObject);
    }

    protected StaticObjectActor(long objectId) {
        super(objectId);
    }
}
