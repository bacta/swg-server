package io.bacta.game.object.universe.city;

import io.bacta.game.object.template.server.ServerObjectTemplate;
import io.bacta.game.object.universe.UniverseObject;
import io.bacta.shared.container.SlotIdManager;
import io.bacta.shared.template.ObjectTemplateList;

public class CityObject extends UniverseObject {
    public CityObject(ObjectTemplateList objectTemplateList, SlotIdManager slotIdManager, ServerObjectTemplate template) {
        super(objectTemplateList, slotIdManager, template);
    }
}
