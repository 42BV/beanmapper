/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.converter.impl;

import io.beanmapper.core.converter.AbstractBeanConverter;

/**
 * Converts a string into a boolean.
 *
 * @author Jeroen van Schagen
 * @since Jun 24, 2015
 */
public class StringToBooleanConverter extends AbstractBeanConverter<String, Boolean> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected Boolean doConvert(String source, Class<? extends Boolean> targetClass) {
        return Boolean.parseBoolean(source);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isMatchingTarget(Class<?> targetClass) {
        return super.isMatchingTarget(targetClass) || boolean.class.equals(targetClass);
    }

}
