package io.beanmapper.exceptions;

public class BeanConfigurationException extends RuntimeException {

    public BeanConfigurationException(final String message) {
        super(message);
    }

    public BeanConfigurationException(final String message, Throwable cause) {
        super(message, cause);
    }

}
