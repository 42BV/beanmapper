/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.converter.impl;

import io.beanmapper.BeanMapper;
import io.beanmapper.core.converter.BeanConverter;

/**
 * Converts any number to another number type.
 *
 * @author Jeroen van Schagen
 * @since Jun 24, 2015
 */
public class NumberToNumberConverter implements BeanConverter {
    
    /**
     * Bean mapper, used to delegate conversions.
     */
    private final BeanMapper beanMapper;
    
    /**
     * Convert a new number to number converter.
     * @param beanMapper the bean mapper
     */
    public NumberToNumberConverter(BeanMapper beanMapper) {
        this.beanMapper = beanMapper;
    }

    /**
     * {@inheritDoc}
     * <br>
     * Works by first converting the number into a string and
     * then converting that string back into the target number.
     */
    @Override
    public Object convert(Object source, Class<?> targetClass) {
        Object sourceAsString = beanMapper.convert(source, String.class);
        return beanMapper.convert(sourceAsString, targetClass);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean match(Class<?> sourceClass, Class<?> targetClass) {
        return Number.class.isAssignableFrom(sourceClass) && 
               Number.class.isAssignableFrom(targetClass);
    }
    
}
