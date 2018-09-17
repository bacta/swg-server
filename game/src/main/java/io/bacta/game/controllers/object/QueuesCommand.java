package io.bacta.game.controllers.object;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by crush on 5/29/2016.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface QueuesCommand {
    /**
     * @return The name of the command that this QueuesCommand will handle.
     */
    String value();
}
