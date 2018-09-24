package io.bacta.swg.template.definition;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by crush on 4/27/2016.
 * <p>
 * Demarcates that this class is a TemplateDefinition class.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TemplateDefinition {
    String value() default "registerTemplateConstructors";
}
