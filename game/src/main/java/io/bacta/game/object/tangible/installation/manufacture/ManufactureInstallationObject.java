package io.bacta.game.object.tangible.installation.manufacture;

import io.bacta.game.object.tangible.installation.InstallationObject;
import io.bacta.game.object.template.server.ServerObjectTemplate;
import io.bacta.shared.container.SlotIdManager;
import io.bacta.shared.template.ObjectTemplateList;

public class ManufactureInstallationObject extends InstallationObject {
    public ManufactureInstallationObject(ObjectTemplateList objectTemplateList, SlotIdManager slotIdManager, ServerObjectTemplate template) {
        super(objectTemplateList, slotIdManager, template);
    }
}
