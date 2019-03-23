package io.bacta.game.object.tangible.installation.manufacture;

import io.bacta.game.object.tangible.installation.InstallationObjectActor;

public class ManufactureInstallationObjectActor extends InstallationObjectActor<ManufactureInstallationObject> {
    protected ManufactureInstallationObjectActor(ManufactureInstallationObject tangibleObject) {
        super(tangibleObject);
    }

    protected ManufactureInstallationObjectActor(long objectId) {
        super(objectId);
    }
}
