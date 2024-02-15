/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.exceptions;

import io.beanmapper.utils.BeanMapperLogger;

/**
 * Exception thrown when a property could not be found.
 */
public class BeanNoSuchPropertyException extends IllegalArgumentException {

    public BeanNoSuchPropertyException(String message) {
        super(message);
        BeanMapperLogger.error(message);
    }

}
