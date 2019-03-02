package io.bacta.game.object;

import io.bacta.game.object.template.server.ServerObjectTemplate;
import io.bacta.shared.container.SlotIdManager;
import io.bacta.shared.template.ObjectTemplateList;

/**
 * Due to Java language restrictions, this can't go in a 'static' package, so it will just go into
 * the object package with ServerObject.
 */
public class StaticObject extends ServerObject {
    public StaticObject(ObjectTemplateList objectTemplateList, SlotIdManager slotIdManager, ServerObjectTemplate template, boolean hyperspaceOnCreate) {
        super(objectTemplateList, slotIdManager, template, hyperspaceOnCreate);
    }
}
