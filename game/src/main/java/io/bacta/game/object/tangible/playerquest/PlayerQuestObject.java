package io.bacta.game.object.tangible.playerquest;

import io.bacta.game.object.tangible.TangibleObject;
import io.bacta.game.object.template.server.ServerObjectTemplate;
import io.bacta.shared.container.SlotIdManager;
import io.bacta.shared.template.ObjectTemplateList;

public class PlayerQuestObject extends TangibleObject {
    public PlayerQuestObject(ObjectTemplateList objectTemplateList, SlotIdManager slotIdManager, ServerObjectTemplate template) {
        super(objectTemplateList, slotIdManager, template);
    }
}
