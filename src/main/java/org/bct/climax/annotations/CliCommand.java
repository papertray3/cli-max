package org.bct.climax.annotations;

import org.apache.commons.cli.HelpFormatter;

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
    public String syntaxPrefix() default HelpFormatter.DEFAULT_SYNTAX_PREFIX;
}
