package io.beanmapper.exceptions;

public class BeanSetFieldException extends BeanMappingException {

    public static final String ERROR = "Not possible to set field %s.%s";

    public BeanSetFieldException(Class<?> classToInstantiate, String fieldName) {
        this(classToInstantiate, fieldName, null);
    }

    public BeanSetFieldException(Class<?> classToInstantiate, String fieldName, Throwable rootCause) {
        super(String.format(ERROR, classToInstantiate.getName(), fieldName), rootCause);
    }

}
