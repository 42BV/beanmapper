/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.converter.impl;

import io.beanmapper.core.converter.AbstractBeanConverter;

/**
 * Converts a string into an integer.
 *
 * @author Jeroen van Schagen
 * @since Jun 24, 2015
 */
public class StringToIntegerConverter extends AbstractBeanConverter<String, Integer> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected Integer doConvert(String source, Class<? extends Integer> targetClass) {
        return Integer.parseInt(source);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isMatchingTarget(Class<?> targetClass) {
        return super.isMatchingTarget(targetClass) || int.class.equals(targetClass);
    }

}
