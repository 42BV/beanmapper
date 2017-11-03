package io.beanmapper.exceptions;

public class BeanFieldNoMatchException extends BeanMappingException {

    public static final String ERROR = "No source field found while attempting to map to %s.%s";

    public BeanFieldNoMatchException(Class clazz, String fieldName) {
        super(String.format(ERROR, clazz, fieldName));
    }

}
