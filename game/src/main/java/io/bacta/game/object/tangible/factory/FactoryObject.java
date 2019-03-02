package io.bacta.game.object.tangible.factory;

import io.bacta.archive.delta.AutoDeltaInt;
import io.bacta.archive.delta.AutoDeltaLong;
import io.bacta.archive.delta.map.AutoDeltaObjectFloatMap;
import io.bacta.game.object.tangible.TangibleObject;
import io.bacta.game.object.template.server.ServerObjectTemplate;
import io.bacta.shared.container.SlotIdManager;
import io.bacta.shared.localization.StringId;
import io.bacta.shared.template.ObjectTemplateList;

public class FactoryObject extends TangibleObject {
    private final AutoDeltaObjectFloatMap<StringId> attributes;
    /**
     * The crafting schematic being used during a crafting session.
     */
    private final AutoDeltaLong craftingSchematic;
    /**
     * The number of objects being usd in the schematic.
     */
    private final AutoDeltaInt craftingCount;

    public FactoryObject(ObjectTemplateList objectTemplateList, SlotIdManager slotIdManager, ServerObjectTemplate template) {
        super(objectTemplateList, slotIdManager, template);

        attributes = new AutoDeltaObjectFloatMap<>(StringId::new);
        craftingSchematic = new AutoDeltaLong();
        craftingCount = new AutoDeltaInt();

        addMembersToPackages();
    }

    private void addMembersToPackages() {
        serverPackageNp.addVariable(attributes);
        serverPackageNp.addVariable(craftingSchematic);
        serverPackageNp.addVariable(craftingCount);
    }
}
