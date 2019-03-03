package io.bacta.game.object.universe.guild;

import io.bacta.archive.delta.set.AutoDeltaStringSet;
import io.bacta.game.object.template.server.ServerObjectTemplate;
import io.bacta.game.object.universe.UniverseObject;
import io.bacta.shared.container.SlotIdManager;
import io.bacta.shared.template.ObjectTemplateList;

import javax.inject.Inject;

public final class GuildObject extends UniverseObject {

    private final AutoDeltaStringSet abbrevs;

    @Inject
    public GuildObject(final ObjectTemplateList objectTemplateList,
                       final SlotIdManager slotIdManager,
                       final ServerObjectTemplate template) {
        super(objectTemplateList, slotIdManager, template);

        abbrevs = new AutoDeltaStringSet();

        sharedPackage.addVariable(abbrevs);
    }
}
