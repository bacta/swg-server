package io.bacta.game.object.intangible.mission;

import io.bacta.game.object.intangible.IntangibleObject;
import io.bacta.game.object.template.server.ServerObjectTemplate;
import io.bacta.shared.container.SlotIdManager;
import io.bacta.shared.template.ObjectTemplateList;

public class MissionObject extends IntangibleObject {
    public MissionObject(ObjectTemplateList objectTemplateList, SlotIdManager slotIdManager, ServerObjectTemplate template) {
        super(objectTemplateList, slotIdManager, template);
    }
}
