package io.beanmapper.exceptions;

public abstract class MappingException extends RuntimeException {

    protected MappingException(final String message) {
        super(message);
    }

    protected MappingException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
