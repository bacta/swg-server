package bacta.io.db;

import java.lang.annotation.*;

/**
 * Created by kyle on 4/3/2017.
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
@Documented
public @interface Repository {
}
