package io.beanmapper.core;

import io.beanmapper.exceptions.BeanMappingException;

public class BeanPropertyAccessException extends BeanMappingException {

    public BeanPropertyAccessException(String message) {
        super(message);
    }

    public BeanPropertyAccessException(Exception e, String message) {
        super(message, e);
    }

}
