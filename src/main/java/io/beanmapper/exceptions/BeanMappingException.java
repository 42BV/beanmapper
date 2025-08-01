package io.beanmapper.exceptions;

public abstract class BeanMappingException extends RuntimeException {

    protected BeanMappingException(String message) {
        super(message);
    }

    protected BeanMappingException(String message, Throwable cause) {
        super(message, cause);
    }

}
