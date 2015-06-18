package io.beanmapper.exceptions;

public abstract class BeanMappingException extends RuntimeException {

    public BeanMappingException(String message) {
        super(message);
    }

    public BeanMappingException(String message, Throwable cause) {
        super(message, cause);
    }

}
