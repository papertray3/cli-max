package org.bct.climax.annotations;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.bct.climax.HelpMode;
import org.bct.climax.ParseMode;

import java.lang.annotation.*;

/**
 * Created by sthatcher on 5/9/14.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface CliSettings {
    public Class<?> stopClass() default Object.class;
    public Class<? extends CommandLineParser> parser() default DefaultParser.class;
    public HelpMode helpMode() default HelpMode.EXPLICIT;
    public ParseMode parseMode() default ParseMode.LAZY;
}
