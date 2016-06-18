package pw.haze.client.management.command.utility;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Haze
 * @since 9/24/2015
 * Used for string parameters, and clamps them to the requires char length.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface LengthClamp {
    int value() default 256;
}
