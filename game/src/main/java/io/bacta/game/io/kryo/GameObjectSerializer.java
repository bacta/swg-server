package io.bacta.game.io.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import io.bacta.engine.object.NetworkObject;
import io.bacta.game.object.GameObjectConstructorMap;
import io.bacta.game.object.ObjectTemplateService;
import io.bacta.game.object.template.server.ServerObjectTemplate;
import io.bacta.swg.container.SlotIdManager;
import io.bacta.swg.object.GameObject;
import io.bacta.swg.template.ObjectTemplateList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



/**
 * Created by kburkhardt on 8/22/14.
 */
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public final class GameObjectSerializer extends Serializer<GameObject> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameObjectSerializer.class);

    private final GameObjectConstructorMap constructorMap;

    private final ObjectTemplateList objectTemplateList;
    private final SlotIdManager slotIdManager;
    private final ObjectTemplateService objectTemplateService;

    private final Map<Class<? extends NetworkObject>, List<Field>> serializableFieldsMap;

    @Inject
    public GameObjectSerializer(final ObjectTemplateList objectTemplateList,
                                final SlotIdManager slotIdManager,
                                final GameObjectConstructorMap constructorMap,
                                final ObjectTemplateService objectTemplateService) {

        this.objectTemplateList = objectTemplateList;
        this.slotIdManager = slotIdManager;
        this.constructorMap = constructorMap;
        this.serializableFieldsMap = new ConcurrentHashMap<>();
        this.objectTemplateService = objectTemplateService;

        loadSerializableClasses();
    }

    private void loadSerializableClasses() {
//        final Reflections reflections = new Reflections();
//        final Set<Class<? extends NetworkObject>> subTypes = reflections.getSubTypesOf(NetworkObject.class);
//        subTypes.forEach(this::loadSerializableClass);
    }

    private void loadSerializableClass(Class<? extends NetworkObject> networkObject) {

        if (networkObject.getSuperclass() != Object.class) {
            loadSerializableClass((Class<? extends NetworkObject>) networkObject.getSuperclass());
        }

        List<Field> fields = serializableFieldsMap.get(networkObject);

        if (fields != null) {
            return;
        }

        fields = new LinkedList<>();


        for (Field field : networkObject.getDeclaredFields()) {

            if (!Modifier.isTransient(field.getModifiers()) &&
                    !Modifier.isStatic(field.getModifiers())) {
                field.setAccessible(true);
                fields.add(field);
            }
        }

        verifyDataVersions(networkObject, fields);
        serializableFieldsMap.put(networkObject, fields);
    }

    private void verifyDataVersions(final Class<? extends NetworkObject> networkObject, final List<Field> fields) {


    }

    @Override
    public final void write(final Kryo kryo, final Output output, final GameObject object) {
        LOGGER.trace("Serializing: {}", object);

        String templateName = object.getObjectTemplate().getResourceName();
        kryo.writeObject(output, templateName);

        writeClassFields(kryo, output, object.getClass(), object);
    }

    private void writeClassFields(final Kryo kryo, final Output output, final Class<? extends NetworkObject> objectClass, final NetworkObject object) {

        if (objectClass.getSuperclass() != Object.class) {
            writeClassFields(kryo, output, (Class<? extends NetworkObject>) objectClass.getSuperclass(), object);
        }

        LOGGER.trace("Writing fields: from {}", objectClass);

        final List<Field> fields = serializableFieldsMap.get(objectClass);
        for (final Field field : fields) {
            try {

                LOGGER.trace("Writing field: {} [{}] ({}) ({})", field.getName(), field.get(object), field.getType(), objectClass.getSimpleName());
                kryo.writeClassAndObject(output, field.get(object));

            } catch (IllegalAccessException e) {
                LOGGER.error("Unable to serialize", e);
            } catch (IllegalArgumentException e) {
                LOGGER.error("Offending field: {} from {}", field.getName(), objectClass, e);
            }
        }
    }

    @Override
    public final GameObject read(final Kryo kryo, final Input input, final Class<GameObject> type) {

        LOGGER.trace("Starting deserialization of {}", type);

        String templatePath = kryo.readObject(input, String.class);
        LOGGER.trace("Found Template {}", templatePath);
        final ServerObjectTemplate serverObjectTemplate = objectTemplateService.getObjectTemplate(templatePath);

        final Constructor<? extends GameObject> constructor = constructorMap.get(type);

        try {

            GameObject newObject = constructor.newInstance(objectTemplateList, slotIdManager, serverObjectTemplate);

            LOGGER.trace("Created Object {}", newObject);

            readClassFields(kryo, input, type, newObject);

            LOGGER.trace("Deserialization of {} complete", type);
            return newObject;

        } catch (IllegalAccessException e) {
            LOGGER.error("Unable to serialize", e);
        } catch (InstantiationException e) {
            LOGGER.error("Unable to instantiate", e);
        } catch (InvocationTargetException e) {
            LOGGER.error("Unable to Invocate", e);
        }

        LOGGER.trace("Deserialization of {} failed", type);
        return null;
    }

    private void readClassFields(Kryo kryo, Input input, Class<? extends NetworkObject> type, NetworkObject newObject) throws IllegalAccessException {

        if (type.getSuperclass() != Object.class) {
            readClassFields(kryo, input, (Class<? extends NetworkObject>) type.getSuperclass(), newObject);
        }

        final List<Field> fields = serializableFieldsMap.get(type);
        for (final Field field : fields) {
            try {
                LOGGER.trace("Found Field: {}  in class {}", field.getName(), type);

                Registration registration = kryo.readClass(input);
                assert registration.getType() == field.getType() : String.format("Registration does not match (%s) (%s)",
                        registration.getType().getSimpleName(), field.getType().getSimpleName());

                field.set(newObject, kryo.readObject(input, field.getType()));
                LOGGER.trace("Field: {} of type {} setting value [{}]", field.getName(), field.getType(), field.get(newObject));

            } catch (Exception e) {
                LOGGER.error("Unable to deserialize {} {}", field.getName(), field.getType(), e);
            }
        }
    }
}