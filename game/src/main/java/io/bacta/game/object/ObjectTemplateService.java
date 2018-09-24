package io.bacta.game.object;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import io.bacta.game.object.intangible.IntangibleObject;
import io.bacta.game.object.intangible.player.PlayerObject;
import io.bacta.game.object.tangible.TangibleObject;
import io.bacta.game.object.tangible.creature.CreatureObject;
import io.bacta.game.object.template.server.*;
import io.bacta.swg.foundation.CrcString;
import io.bacta.swg.foundation.DataResourceList;
import io.bacta.swg.iff.Iff;
import io.bacta.swg.template.ObjectTemplate;
import io.bacta.swg.template.ObjectTemplateList;
import io.bacta.swg.template.definition.TemplateDefinition;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.lang.reflect.Method;

@Slf4j
@Service
public class ObjectTemplateService {

    private final TIntObjectMap<Class<? extends ServerObject>> templateClassMap = new TIntObjectHashMap<>(100);
    private final ObjectTemplateList objectTemplateList;

    @Inject
    public ObjectTemplateService(ObjectTemplateList objectTemplateList) {
        this.objectTemplateList = objectTemplateList;

        registerTemplates();
        configureTemplateClassMap();
    }

    private void registerTemplates() {
        //Registering all template definitions with the object template list.
        try {
            final ClassInfoList classes = new ClassGraph()
                    .enableAllInfo()
                    .scan()
                    .getClassesWithAnnotation(TemplateDefinition.class.getName());

            for (Class<?> classType : classes.loadClasses()) {
                final String registerMethodName = classType.getAnnotation(TemplateDefinition.class).value();

                if (ObjectTemplate.class.isAssignableFrom(classType)) {
                    final Method registerMethod = classType.getDeclaredMethod(registerMethodName, DataResourceList.class);
                    registerMethod.setAccessible(true);
                    registerMethod.invoke(null, objectTemplateList);
                }
            }
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }

    private void configureTemplateClassMap() {
        //TODO: Load with reflection instead.
        templateClassMap.put(ServerObjectTemplate.TAG_SERVEROBJECTTEMPLATE, ServerObject.class);
        templateClassMap.put(ServerTangibleObjectTemplate.TAG_SERVERTANGIBLEOBJECTTEMPLATE, TangibleObject.class);
        templateClassMap.put(ServerCreatureObjectTemplate.TAG_SERVERCREATUREOBJECTTEMPLATE, CreatureObject.class);
        templateClassMap.put(ServerIntangibleObjectTemplate.TAG_SERVERINTANGIBLEOBJECTTEMPLATE, IntangibleObject.class);
        templateClassMap.put(ServerPlayerObjectTemplate.TAG_SERVERPLAYEROBJECTTEMPLATE, PlayerObject.class);
    }

    public <T extends ObjectTemplate> T getObjectTemplate(final String templatePath) {
        return objectTemplateList.fetch(templatePath);
    }

    public <T extends ObjectTemplate> T getObjectTemplate(final CrcString templatePath) {
        return objectTemplateList.fetch(templatePath);
    }

    public <T extends ObjectTemplate> T getObjectTemplate(final int crc) {
        return objectTemplateList.fetch(crc);
    }

    /**
     * This gets the class associated with the provided template type
     * this is used in object player.
     *
     * @param template template to get class object for
     * @return Class object related to the specified type
     */
    @SuppressWarnings("unchecked")
    public <T extends ServerObject> Class<T> getClassForTemplate(final ObjectTemplate template) {
        final Class<T> classType = (Class<T>) templateClassMap.get(template.getId());

        if (classType == null)
            LOGGER.error("Did not find template with class mapping: " + Iff.getChunkName(template.getId()));

        return classType;
    }
}
