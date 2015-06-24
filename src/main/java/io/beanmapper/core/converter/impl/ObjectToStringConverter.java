/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.converter.impl;

import io.beanmapper.core.converter.AbstractBeanConverter;

/**
 * Converts objects into strings.
 *
 * @author Jeroen van Schagen
 * @since Jun 24, 2015
 */
public class ObjectToStringConverter extends AbstractBeanConverter<Object, String> {
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected Object doConvert(Object source, Class<? extends String> targetClass) {
        return source.toString();
    }
    
}
