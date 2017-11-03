package io.beanmapper.exceptions;


public class BeanMissingPathException extends BeanMappingException {

    public static final String ERROR = "The path for the class could not be resolved %s.%s";

    public BeanMissingPathException(Class<?> clazz, String name, Throwable rootCause) {
        super(String.format(ERROR, clazz.getName(), name), rootCause);
    }

}
