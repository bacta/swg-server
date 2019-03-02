package io.bacta.game.object.tangible.installation;

import io.bacta.game.object.tangible.TangibleObjectActor;

public class InstallationObjectActor<T extends InstallationObject> extends TangibleObjectActor<T> {
    protected InstallationObjectActor(T tangibleObject) {
        super(tangibleObject);
    }
}
