package io.bacta.game.object.attributes;

import io.bacta.game.object.ServerObject;
import io.bacta.swg.object.AttributeList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Map;

/**
 * Helps to service requests for an object's attributes by running the object through a series of factories as needed.
 */
@Slf4j
@Service
public final class AttributeListService {
    private static final int MAX_ATTRIBUTES_PLAYER = 80;
    private static final int MAX_ATTRIBUTES_GOD = 2000;

    private final Map<Class<? extends ServerObject>, AttributeListAppender> appenders;

    @Inject
    public AttributeListService(final AttributeListAppenderLoader loader) {
        appenders = loader.load();
    }

    public AttributeList request(final ServerObject requestor, final ServerObject object) {
        int capacity = MAX_ATTRIBUTES_PLAYER;

        //TODO: Implement god level.
//        if (requestor.isGod()) {
//            capacity = MAX_ATTRIBUTES_ADMIN;
//        }

        final AttributeList attributeList = new AttributeList(capacity);

        return get(object, attributeList);
    }

    @SuppressWarnings("unchecked")
    public <T extends ServerObject> AttributeList get(final T object, final AttributeList attributeList) {
        //Lookup the appender in the map.
        final Class<? extends ServerObject> objectClass = object.getClass();

        if (!appenders.containsKey(objectClass)) {
            throw new IllegalStateException(
                    String.format("Expected to have appender for ServerObject class %s, but did not find one.",
                            objectClass.getName()));
        }


        final AttributeListAppender appender = appenders.get(objectClass);
        appender.append(object, attributeList);

        return attributeList;
    }
}
