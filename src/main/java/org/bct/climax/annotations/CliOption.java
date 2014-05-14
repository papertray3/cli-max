package org.bct.climax.annotations;

import org.bct.climax.OptionNaming;

import java.lang.annotation.*;

/**
 * Created by sthatcher on 5/9/14.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface CliOption {
    public String shortOption() default "";
    public String longOption() default "";
    public String description() default "";
    public String name() default "";
    public String defaultValue() default "";
    public String argumentHint() default "";
    public OptionNaming namingHint() default OptionNaming.SHORT;
}
