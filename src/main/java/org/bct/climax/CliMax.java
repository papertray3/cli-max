package org.bct.climax;

import org.apache.commons.cli.Options;
import org.bct.climax.annotations.CliCommand;
import org.bct.climax.annotations.CliSettings;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by sthatcher on 5/9/14.
 */
public class CliMax {

    private static class AnnotationDefaults implements InvocationHandler {

        public static <A extends Annotation> A of(Class<A> annotation) {

            return (A) Proxy.newProxyInstance(annotation.getClassLoader(),
                    new Class[] {annotation}, new AnnotationDefaults());
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return method.getDefaultValue();
        }
    }

    private Object target;
    private Options options;
    private String name;
    private String header;
    private String footer;

    // classes for defaults of annotations
    private CliSettings aCliSettings = AnnotationDefaults.of(CliSettings.class);

    public CliMax(Object target) {

        this.target = target;

        CliCommand command = target.getClass().getAnnotation(CliCommand.class);
        if (command != null) {
            command = AnnotationDefaults.of(CliCommand.class);
        }

        if (command.name().isEmpty())
            name = target.getClass().getSimpleName();

        header = command.header();
        footer = command.footer();

        CliSettings settings = target.getClass().getAnnotation(CliSettings.class);
        if (settings != null)
            aCliSettings = settings;

        createOptions();
    }

    public CliMax(Object target, String[] args) {

        this(target);
        parse(args);

    }


    public void parse(String[] args) {

    }


    private void createOptions() {



    }


}
