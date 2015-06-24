/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.exceptions;

/**
 * 
 *
 * @author Jeroen van Schagen
 * @since Jun 24, 2015
 */
public class NoSuchPropertyException extends IllegalArgumentException {
    
    public NoSuchPropertyException(String message) {
        super(message);
    }
    
}
