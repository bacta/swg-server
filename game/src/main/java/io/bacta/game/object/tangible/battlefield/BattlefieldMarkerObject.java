package io.bacta.game.object.tangible.battlefield;

import io.bacta.game.object.tangible.TangibleObject;
import io.bacta.game.object.template.server.ServerObjectTemplate;
import io.bacta.shared.container.SlotIdManager;
import io.bacta.shared.template.ObjectTemplateList;

public class BattlefieldMarkerObject extends TangibleObject {
    public BattlefieldMarkerObject(ObjectTemplateList objectTemplateList, SlotIdManager slotIdManager, ServerObjectTemplate template) {
        super(objectTemplateList, slotIdManager, template);
    }
}
