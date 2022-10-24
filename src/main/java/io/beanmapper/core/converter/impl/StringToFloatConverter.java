/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.converter.impl;

import io.beanmapper.core.converter.SimpleBeanConverter;

/**
 * Converts a string into a float.
 *
 * @author Jeroen van Schagen
 * @since Jun 24, 2015
 */
public class StringToFloatConverter extends SimpleBeanConverter<String, Float> {

    public StringToFloatConverter() {
        super(String.class, Float.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Float doConvert(String source) {
        return Float.parseFloat(source);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isMatchingTarget(Class<?> targetClass) {
        return super.isMatchingTarget(targetClass) || float.class.equals(targetClass);
    }

}
