package io.beanmapper.exceptions;

public class BeanPropertyNoMatchException extends BeanMappingException {

    public static final String ERROR = "No source field found while attempting to map to %s.%s";

    public BeanPropertyNoMatchException(Class<?> clazz, String fieldName) {
        super(ERROR.formatted(clazz, fieldName));
    }

}
