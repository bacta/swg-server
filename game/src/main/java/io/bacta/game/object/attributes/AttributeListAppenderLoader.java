package io.bacta.game.object.attributes;

import io.bacta.game.object.ServerObject;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Scope("prototype")
@Component
public final class AttributeListAppenderLoader implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    public Map<Class<? extends ServerObject>, AttributeListAppender> load() {
        final Map<Class<? extends ServerObject>, AttributeListAppender> appenders = new HashMap<>();

        final String[] appenderBeanNames = applicationContext.getBeanNamesForType(AttributeListAppender.class);

        for (final String appenderBeanName : appenderBeanNames) {
            final AttributeListAppender appender = (AttributeListAppender) applicationContext.getBean(appenderBeanName);
            loadAppender(appenders, appender);
        }

        configureMissingAppenders(appenders);

        return appenders;
    }

    private void loadAppender(Map<Class<? extends ServerObject>, AttributeListAppender> appenders, AttributeListAppender appender) {
        try {
            final AppendsAttributesFor appenderAnnotation = appender.getClass().getAnnotation(AppendsAttributesFor.class);

            if (appenderAnnotation == null) {
                LOGGER.warn("Missing @AppendsAttributesFor annotation. Discarding appender {}.",
                        appender.getClass().getName());
                return;
            }

            final Class<? extends ServerObject> objectClass = appenderAnnotation.value();

            if (appenders.containsKey(objectClass)) {
                final AttributeListAppender existingAppender = appenders.get(objectClass);

                LOGGER.error("Already loaded appender {} for server objects of type {}. Discarding appender {}.",
                        existingAppender.getClass().getName(),
                        objectClass.getSimpleName(),
                        appender.getClass().getName());

                return;
            }

            appenders.put(objectClass, appender);

            LOGGER.trace("Loaded attribute list appender {}.", appender.getClass().getName());

        } catch (Throwable exception) {
            LOGGER.error("Unable to add attribute list appender {}.",
                    appender.getClass().getName(),
                    exception);
        }
    }

    /**
     * Any ServerObject types that don't have an explicit appender mapped need to be handled. To prevent having to walk the
     * object inheritance graph at call time, we will just setup O(1) mappings now to the most appropriate appender.
     *
     * @param appenders Map of appenders that have been loaded.
     */
    @SuppressWarnings("unchecked")
    private void configureMissingAppenders(Map<Class<? extends ServerObject>, AttributeListAppender> appenders) {
        try (final ScanResult scanResult = new ClassGraph()
                .enableAllInfo()
                .whitelistPackages("io.bacta.game.object")
                .scan()) {

            //We want to look at each server object game type and see if it has an appender loaded for it.
            //If it doesn't, then we want to find the most suitable appender for the type, and map it instead.
            final ClassInfoList serverObjectClassInfoList = scanResult.getSubclasses(ServerObject.class.getName());

            for (ClassInfo classInfo : serverObjectClassInfoList) {

                final Class<? extends ServerObject> objectClass = (Class<? extends ServerObject>)classInfo.loadClass();

                if (!appenders.containsKey(objectClass)) {
                    LOGGER.debug("Did not find an appender for object type {}. Attempting to find closest appender.",
                            objectClass.getSimpleName());

                    final AttributeListAppender closestAppender = findClosestAppender(appenders, classInfo);

                    if (closestAppender == null) {
                        LOGGER.error("Unable to find an attribute list appender for object type {}.",
                                objectClass.getSimpleName());

                        continue;
                    }

                    LOGGER.debug("Found closest appender {} to handle {}.",
                            closestAppender.getClass().getName(),
                            objectClass.getSimpleName());

                    appenders.put(objectClass, closestAppender);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private AttributeListAppender findClosestAppender(Map<Class<? extends ServerObject>, AttributeListAppender> appenders,
                                                      ClassInfo classInfo) {

        final ClassInfo superClassInfo = classInfo.getSuperclass();

        if (superClassInfo == null) {
            return null; //There is no super class.
        }

        final Class<? extends ServerObject> superObjectType = (Class<? extends ServerObject>)superClassInfo.loadClass();

        if (!appenders.containsKey(superObjectType)){
            return findClosestAppender(appenders, superClassInfo);
        }

        return appenders.get(superObjectType);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
