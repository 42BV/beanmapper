package io.beanmapper.exceptions;

import java.lang.reflect.Field;

public class BeanGetFieldException extends BeanMappingException {

    public static final String ERROR = "Not possible to get field %s.%s";

    public BeanGetFieldException(Class<?> classToInstantiate, Field field, Throwable rootCause) {
        super(String.format(ERROR, classToInstantiate.getName(), field.getName()), rootCause);
    }

}
