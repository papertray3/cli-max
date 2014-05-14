package org.bct.climax.annotations;

import java.lang.annotation.*;

/**
 * Created by sthatcher on 5/9/14.
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface CliCommand {
    public String name() default "";
    public String header() default "";
    public String footer() default "";
}
