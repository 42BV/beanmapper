/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exception thrown when a property could not be found.
 */
public class BeanNoSuchPropertyException extends IllegalArgumentException {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    public BeanNoSuchPropertyException(String message) {
        super(message);
        logger.error(message);
    }

}
