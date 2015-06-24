package io.beanmapper.exceptions;

public class BeanGetFieldException extends BeanMappingException {

    public static final String ERROR = "Not possible to get field %s.%s";

    public BeanGetFieldException(Class<?> classToInstantiate, String fieldName) {
        this(classToInstantiate, fieldName, null);
    }

    public BeanGetFieldException(Class<?> classToInstantiate, String fieldName, Throwable rootCause) {
        super(String.format(ERROR, classToInstantiate.getName(), fieldName), rootCause);
    }

}
