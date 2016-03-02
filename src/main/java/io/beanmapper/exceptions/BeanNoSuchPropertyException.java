/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.exceptions;

/**
 * Exception thrown when a property could not be found.
 *
 * @author Jeroen van Schagen
 * @since Jun 24, 2015
 */
public class BeanNoSuchPropertyException extends IllegalArgumentException {
    
    public BeanNoSuchPropertyException(String message) {
        super(message);
    }
    
}
