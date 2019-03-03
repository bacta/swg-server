package io.bacta.game.object.intangible.schematic;

import io.bacta.game.object.intangible.IntangibleObject;
import io.bacta.game.object.template.server.ServerObjectTemplate;
import io.bacta.shared.container.SlotIdManager;
import io.bacta.shared.template.ObjectTemplateList;

import javax.inject.Inject;

/**
 * Created by crush on 5/8/2016.
 */
public class DraftSchematicObject extends IntangibleObject {
    @Inject
    public DraftSchematicObject(final ObjectTemplateList objectTemplateList,
                                final SlotIdManager slotIdManager,
                                final ServerObjectTemplate template) {
        super(objectTemplateList, slotIdManager, template);
    }
}
