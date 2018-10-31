package io.bacta.game.io.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import io.bacta.game.object.ObjectTemplateService;
import io.bacta.swg.template.ObjectTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * Created by kyle on 6/6/2016.
 */
@Slf4j
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ObjectTemplateSerializer extends Serializer<ObjectTemplate> {
    private final ObjectTemplateService objectTemplateService;

    @Inject
    public ObjectTemplateSerializer(final ObjectTemplateService objectTemplateService) {
        this.objectTemplateService = objectTemplateService;
    }

    @Override
    public void write(Kryo kryo, Output output, ObjectTemplate object) {
        final String templateName = object.getResourceName();
        kryo.writeObject(output, templateName);
        LOGGER.trace("Writing Template: {}", templateName);
    }

    @Override
    public ObjectTemplate read(Kryo kryo, Input input, Class<ObjectTemplate> type) {
        final String templatePath = kryo.readObject(input, String.class);
        LOGGER.trace("Found Template {}", templatePath);
        return objectTemplateService.getObjectTemplate(templatePath);
    }
}