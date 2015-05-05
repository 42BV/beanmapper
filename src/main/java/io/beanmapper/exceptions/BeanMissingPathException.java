package io.beanmapper.exceptions;

import java.lang.reflect.Field;

public class BeanMissingPathException extends BeanMappingException {

    public static final String ERROR = "The path for the class could not be resolved %s.%s";

    public BeanMissingPathException(String error) {
        super(error);
    }

    public BeanMissingPathException(Class clazz, Field field, Throwable rootCause) {
        super(String.format(ERROR, clazz.getName(), field.getName()), rootCause);
    }

}
