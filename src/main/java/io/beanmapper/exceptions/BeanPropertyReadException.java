package io.beanmapper.exceptions;

public class BeanPropertyReadException extends BeanMappingException {

    public static final String ERROR = "Not possible to get property %s.%s";

    public BeanPropertyReadException(Class<?> classToInstantiate, String propertyName) {
        this(classToInstantiate, propertyName, null);
    }

    public BeanPropertyReadException(Class<?> classToInstantiate, String propertyName, Throwable rootCause) {
        super(String.format(ERROR, classToInstantiate.getName(), propertyName), rootCause);
    }

}
