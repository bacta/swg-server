package io.bacta.game.object.universe.group;

import io.bacta.archive.delta.AutoDeltaInt;
import io.bacta.archive.delta.AutoDeltaLong;
import io.bacta.archive.delta.AutoDeltaShort;
import io.bacta.archive.delta.AutoDeltaString;
import io.bacta.archive.delta.vector.AutoDeltaObjectVector;
import io.bacta.game.object.tangible.ship.ShipFormationGroupMember;
import io.bacta.game.object.template.server.ServerObjectTemplate;
import io.bacta.game.object.universe.UniverseObject;
import io.bacta.shared.container.SlotIdManager;
import io.bacta.shared.template.ObjectTemplateList;

import javax.inject.Inject;

public final class GroupObject extends UniverseObject {
    public static class LootRule {
        public static final int FREE_FOR_ALL = 0x00;
        public static final int MASTER_LOOTER = 0x01;
        public static final int LOTTERY = 0x02;
        public static final int RANDOM = 0x03;
    }

    private final AutoDeltaObjectVector<GroupMember> groupMembers;
    private final AutoDeltaObjectVector<ShipFormationGroupMember> groupShipFormationMembers;
    private final AutoDeltaString groupName;
    private final AutoDeltaShort groupLevel;
    private final AutoDeltaInt formationNameCrc;
    private final AutoDeltaLong lootMaster;
    private final AutoDeltaInt lootRule;

    @Inject
    public GroupObject(final ObjectTemplateList objectTemplateList,
                       final SlotIdManager slotIdManager,
                       final ServerObjectTemplate template) {
        super(objectTemplateList, slotIdManager, template);

        groupMembers = new AutoDeltaObjectVector<>(GroupMember::new);
        groupShipFormationMembers = new AutoDeltaObjectVector<>(ShipFormationGroupMember::new);
        groupName = new AutoDeltaString("");
        groupLevel = new AutoDeltaShort();
        formationNameCrc = new AutoDeltaInt();
        lootMaster = new AutoDeltaLong();
        lootRule = new AutoDeltaInt(LootRule.FREE_FOR_ALL);

        addMembersToPackages();
    }

    private void addMembersToPackages() {
        sharedPackageNp.addVariable(groupMembers);
        sharedPackageNp.addVariable(groupShipFormationMembers);
        sharedPackageNp.addVariable(groupName);
        sharedPackageNp.addVariable(groupLevel);
        sharedPackageNp.addVariable(formationNameCrc);
        sharedPackageNp.addVariable(lootMaster);
        sharedPackageNp.addVariable(lootRule);
    }
}
