package org.bct.climax;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.bct.climax.annotations.*;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by sthatcher on 5/9/14.
 */
public class CliMax {

    static public  final String ARGUMENTS_FIELD_NAME = "arguments";
    static private final String SHORT_OPTIONS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";

    private static class AnnotationDefaults implements InvocationHandler {

        public static <A extends Annotation> A of(Class<A> annotation) {

            return (A) Proxy.newProxyInstance(annotation.getClassLoader(),
                    new Class[] {annotation}, new AnnotationDefaults());
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return method.getDefaultValue();
        }
    }

    // external data
    private Object target;
    private Options options;
    private String name;
    private String header;
    private String footer;
    private HelpFormatter helpFormatter = new HelpFormatter();
    private CommandLine cli = null;

    // internal data
    private Field argumentsField = null;
    private CliOption curOption = null;
    private CliSettings settings = null;

    // classes for defaults of annotations
    static private CliSettings aCliSettings = AnnotationDefaults.of(CliSettings.class);
    static private CliOption aCliOption = AnnotationDefaults.of(CliOption.class);

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
        helpFormatter.setSyntaxPrefix(command.syntaxPrefix());

        settings = target.getClass().getAnnotation(CliSettings.class);
        if (settings != null)
            settings = aCliSettings;

        createOptions();
    }

    public CliMax(Object target, String[] args) {

        this(target);
        parse(args);

    }




    public void parse(String[] args) {
    }

    public String getHelp() {
        return null;
    }

    public String getHelp(String message) {
        return null;
    }

    public void printHelp() {

    }

    public void printHelp(String message) {

    }


    // Getters and Setters

    public Options getOptions() {
        return options;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public HelpFormatter getHelpFormatter() {
        return helpFormatter;
    }

    public CommandLine getCli() {
        return cli;
    }


    // privates

    private void createOptions() {

        PropertyUtilsBean pub = new PropertyUtilsBean();
        for(PropertyDescriptor pd : pub.getPropertyDescriptors(target)) {
            Field f = getField(target.getClass(), pd.getName());

            if (f == null || f.getAnnotation(NonOption.class) != null)
                continue;

            if ((f.getName().equals(ARGUMENTS_FIELD_NAME) && aCliSettings.parseMode() == ParseMode.LAZY) ||
                    f.getAnnotation(Arguments.class) != null) {
                argumentsField = f;
                continue;
            }

            curOption = f.getAnnotation(CliOption.class);
            if (aCliSettings.parseMode() == ParseMode.STRICT && curOption == null)
                continue;
            else if (curOption == null)
                curOption = aCliOption;

            Option.Builder builder = Option.builder();

        }

    }

    private Field getField(Class<?> clazz, String fieldName) {
        Class<?> curClass = clazz;
        while(curClass != null && !curClass.equals(aCliSettings.stopClass())) {

            try {
                return curClass.getDeclaredField(fieldName);
            } catch(Exception e)  {

            }

            curClass = curClass.getSuperclass();
        }

        return null;
    }

    private String generateShortOption(String field) {

        Character c = null;
        if (!curOption.shortOption().trim().isEmpty()) {
            c = curOption.shortOption().trim().charAt(0);
            if (!Character.isJavaIdentifierPart(c) || options.getOption(c.toString()) != null) {
                //TODO : throw exception here
                return null;
            } else
                return c.toString();
        }

        for (int i = 0; i < field.length(); i++) {

            c = Character.toLowerCase(field.charAt(i));
            if (Character.isJavaIdentifierPart(c)) {
                if (options.getOption(c.toString()) != null) {
                    c = Character.toUpperCase(c);
                    if (options.getOption(c.toString()) == null)
                        return c.toString();
                }

                return c.toString();
            }
        }

        //last ditch effort
        for (int i = 0; i < SHORT_OPTIONS.length(); i++) {
            String opt = SHORT_OPTIONS.substring(i,i+1);
            if (options.getOption(opt) == null)
                return opt;
        }

        //TODO : throw and exception ?
        return null;

    }

    private String generateLongOption(String field, boolean force) {

        if (curOption.namingHint() == OptionNaming.SHORT && force == false)
            return null;
        String opt = curOption.longOption().trim();
        if (!opt.isEmpty()) {
            if (options.getOption(opt) == null)
                return opt;

        } else
            opt = field;

        StringBuilder sb = new StringBuilder();
        boolean hyphencheck = false;

        for(int i = 0; i < opt.length(); i++) {
            Character c = opt.charAt(i);
            if (hyphencheck && Character.isUpperCase(c)) {
                sb.append("-");
                hyphencheck = false;
            }

            if (Character.isLowerCase(c))
                hyphencheck = true;

            if (Character.isJavaIdentifierPart(c))
                sb.append(c);
        }

        if (sb.length() == 0) // TODO: throw exception if force is true
            return null;

        String ret = sb.toString().toLowerCase();
        if (options.getOption(ret) != null) //TODO: do something more here
            return null;


        return ret;
    }


}
