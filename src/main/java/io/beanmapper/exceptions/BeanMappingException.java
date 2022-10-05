package io.beanmapper.exceptions;

public abstract class BeanMappingException extends MappingException {

    protected BeanMappingException(String message) {
        super(message);
    }

    protected BeanMappingException(String message, Throwable cause) {
        super(message, cause);
    }

}
