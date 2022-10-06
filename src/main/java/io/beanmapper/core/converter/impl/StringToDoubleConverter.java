/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.converter.impl;

import io.beanmapper.core.converter.SimpleBeanConverter;

/**
 * Converts a string into a double.
 *
 * @author Jeroen van Schagen
 * @since Jun 24, 2015
 */
public class StringToDoubleConverter extends SimpleBeanConverter<String, Double> {

    public StringToDoubleConverter() {
        super(String.class, Double.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Double doConvert(String source) {
        return Double.parseDouble(source);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isMatchingTarget(Class<?> targetClass) {
        return super.isMatchingTarget(targetClass) || double.class.equals(targetClass);
    }

}
