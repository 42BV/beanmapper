package io.beanmapper.exceptions;

public class BeanPropertyWriteException extends BeanMappingException {

    public static final String ERROR = "Not possible to set property %s.%s";

    public BeanPropertyWriteException(Class<?> classToInstantiate, String propertyName) {
        this(classToInstantiate, propertyName, null);
    }

    public BeanPropertyWriteException(Class<?> classToInstantiate, String propertyName, Throwable rootCause) {
        super(ERROR.formatted(classToInstantiate.getName(), propertyName), rootCause);
    }

}
