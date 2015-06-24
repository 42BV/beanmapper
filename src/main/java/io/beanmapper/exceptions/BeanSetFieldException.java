package io.beanmapper.exceptions;

import java.lang.reflect.Field;

public class BeanSetFieldException extends BeanMappingException {

    public static final String ERROR = "Not possible to set field %s.%s";

    public BeanSetFieldException(Class<?> classToInstantiate, Field field, Throwable rootCause) {
        super(String.format(ERROR, classToInstantiate.getName(), field.getName()), rootCause);
    }

}
