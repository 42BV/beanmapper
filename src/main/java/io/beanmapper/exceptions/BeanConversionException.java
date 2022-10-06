/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.exceptions;

/**
 * Exception thrown when cannot convert between types.
 *
 * @author Jeroen van Schagen
 * @since Jun 24, 2015
 */
public class BeanConversionException extends BeanMappingException {

    public static final String ERROR = "Could not convert %s to %s.";

    public BeanConversionException(Class<?> sourceClass, Class<?> targetClass) {
        super(ERROR.formatted(sourceClass.getSimpleName(), targetClass.getSimpleName()));
    }

}
