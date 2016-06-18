package pw.haze.client.management.command.utility;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Haze
 * @since 9/24/2015
 * Used for digit parameters, and clamps them.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface DigitClamp {
    float min() default 0;

    float max() default 10;
}
