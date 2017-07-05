package io.bacta.game;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by kyle on 5/5/2016.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Priority {
    short value();
}
