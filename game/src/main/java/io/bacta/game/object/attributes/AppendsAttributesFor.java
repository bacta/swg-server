package io.bacta.game.object.attributes;

import io.bacta.game.object.ServerObject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AppendsAttributesFor {
    Class<? extends ServerObject> value();
}
