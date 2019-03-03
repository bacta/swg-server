package io.bacta.game.object;

import io.bacta.game.object.tangible.creature.CreatureObject;
import io.bacta.game.object.template.server.ServerObjectTemplate;
import io.bacta.shared.container.SlotIdManager;
import io.bacta.shared.template.ObjectTemplateList;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component
public class ServerObjectFactory {
    private final ObjectTemplateList objectTemplateList;
    private final SlotIdManager slotIdManager;

    @Inject()
    public ServerObjectFactory(ObjectTemplateList objectTemplateList, SlotIdManager slotIdManager) {
        this.objectTemplateList = objectTemplateList;
        this.slotIdManager = slotIdManager;
    }

    @SuppressWarnings("unchecked")
    public <T extends ServerObject> T createUsingTemplate(Class<T> objectClass, String templatePath) {
        final ServerObjectTemplate template = objectTemplateList.fetch(templatePath);
        return (T) new CreatureObject(objectTemplateList, slotIdManager, template);
    }
}
