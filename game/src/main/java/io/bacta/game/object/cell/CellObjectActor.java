package io.bacta.game.object.cell;

import io.bacta.game.object.ServerObjectActor;

public class CellObjectActor extends ServerObjectActor<CellObject> {
    protected CellObjectActor(CellObject serverObject) {
        super(serverObject);
    }

    protected CellObjectActor(long objectId) {
        super(objectId);
    }
}
