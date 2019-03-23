package io.bacta.game.object.tangible;

import akka.japi.pf.ReceiveBuilder;
import io.bacta.game.object.ServerObjectActor;

public class TangibleObjectActor<T extends TangibleObject> extends ServerObjectActor<T> {
    protected TangibleObjectActor(T tangibleObject) {
        super(tangibleObject);
    }

    protected TangibleObjectActor(long objectId) {
        super(objectId);
    }

    @Override
    protected ReceiveBuilder appendReceiveHandlers(ReceiveBuilder receiveBuilder) {
        return super.appendReceiveHandlers(receiveBuilder);
    }
}