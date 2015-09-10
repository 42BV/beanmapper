/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.converter.impl;

import io.beanmapper.BeanMapper;
import io.beanmapper.core.BeanFieldMatch;
import io.beanmapper.core.converter.BeanConverter;

import java.util.HashSet;
import java.util.Set;

/**
 * Converts any number to another number type.
 *
 * @author Jeroen van Schagen
 * @since Jun 24, 2015
 */
public class NumberToNumberConverter implements BeanConverter {
    
    private static final Set<Class<?>> PRIMITIVES = new HashSet<Class<?>>();
    
    static {
        PRIMITIVES.add(byte.class);
        PRIMITIVES.add(short.class);
        PRIMITIVES.add(int.class);
        PRIMITIVES.add(long.class);
        PRIMITIVES.add(float.class);
        PRIMITIVES.add(double.class);
    }

    /**
     * Bean mapper, used to delegate conversions.
     */
    private BeanMapper beanMapper;

    public void setBeanMapper(BeanMapper beanMapper) {
        this.beanMapper = beanMapper;
    }

    /**
     * {@inheritDoc}
     * <br>
     * Works by first converting the number into a string and
     * then converting that string back into the target number.
     */
    @Override
    public Object convert(Object source, Class<?> targetClass, BeanFieldMatch beanFieldMatch) {
        if (source == null || source.getClass().equals(targetClass) || (beanFieldMatch != null && beanFieldMatch.getSourceClass().equals(targetClass))) {
            return source;
        }
        Object sourceAsString = beanMapper.convert(source, String.class, beanFieldMatch);
        return beanMapper.convert(sourceAsString, targetClass, beanFieldMatch);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean match(Class<?> sourceClass, Class<?> targetClass) {
        return isNumber(sourceClass) && isNumber(targetClass);
    }

    private boolean isNumber(Class<?> clazz) {
        return Number.class.isAssignableFrom(clazz) || PRIMITIVES.contains(clazz);
    }
    
}
