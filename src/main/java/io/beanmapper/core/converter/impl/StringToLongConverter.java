/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.converter.impl;

import io.beanmapper.core.converter.AbstractBeanConverter;

/**
 * Converts a string into a long.
 *
 * @author Jeroen van Schagen
 * @since Jun 24, 2015
 */
public class StringToLongConverter extends AbstractBeanConverter<String, Long> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected Long doConvert(String source, Class<? extends Long> targetClass) {
        return Long.parseLong(source);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isMatchingTarget(Class<?> targetClass) {
        return super.isMatchingTarget(targetClass) || long.class.equals(targetClass);
    }

}
