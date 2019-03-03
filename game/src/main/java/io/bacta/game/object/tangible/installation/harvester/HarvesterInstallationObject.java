package io.bacta.game.object.tangible.installation.harvester;

import io.bacta.game.object.tangible.installation.InstallationObject;
import io.bacta.game.object.template.server.ServerObjectTemplate;
import io.bacta.shared.container.SlotIdManager;
import io.bacta.shared.template.ObjectTemplateList;

public class HarvesterInstallationObject extends InstallationObject {
    public HarvesterInstallationObject(ObjectTemplateList objectTemplateList, SlotIdManager slotIdManager, ServerObjectTemplate template) {
        super(objectTemplateList, slotIdManager, template);
    }
}
