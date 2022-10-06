package io.beanmapper.exceptions;

public class BeanConstructException extends BeanMappingException {

    public static final String ERROR = "No suitable constructor found while trying to bean construct class %s";

    public BeanConstructException(Class<?> classToInstantiate, Throwable rootCause) {
        super(ERROR.formatted(classToInstantiate.getName()), rootCause);
    }

}
