package io.bacta.game.object.cell;

import io.bacta.archive.delta.AutoDeltaBoolean;
import io.bacta.archive.delta.AutoDeltaInt;
import io.bacta.archive.delta.AutoDeltaVariable;
import io.bacta.engine.lang.UnicodeString;
import io.bacta.game.object.ServerObject;
import io.bacta.game.object.template.server.ServerObjectTemplate;
import io.bacta.shared.container.SlotIdManager;
import io.bacta.shared.math.Vector;
import io.bacta.shared.template.ObjectTemplateList;

import javax.inject.Inject;

/**
 * Created by crush on 8/18/2014.
 */
public class CellObject extends ServerObject {
    private final AutoDeltaBoolean isPublic;
    private final AutoDeltaInt cellNumber;
    private final AutoDeltaVariable<UnicodeString> cellLabel;
    private final AutoDeltaVariable<Vector> labelLocationOffset;

    @Inject
    public CellObject(final ObjectTemplateList objectTemplateList,
                      final SlotIdManager slotIdManager,
                      final ServerObjectTemplate template) {
        super(objectTemplateList, slotIdManager, template, false);

        isPublic = new AutoDeltaBoolean(true);
        cellNumber = new AutoDeltaInt(0);
        cellLabel = new AutoDeltaVariable<>(UnicodeString.EMPTY, UnicodeString::new);
        labelLocationOffset = new AutoDeltaVariable<>(new Vector(Vector.ZERO), Vector::new);
    }

    private void addMembersToPackages() {
        sharedPackage.addVariable(isPublic);
        sharedPackage.addVariable(cellNumber);
        sharedPackageNp.addVariable(cellLabel);
        sharedPackageNp.addVariable(labelLocationOffset);
    }
}
