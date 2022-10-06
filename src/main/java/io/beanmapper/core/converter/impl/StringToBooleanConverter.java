/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.converter.impl;

import io.beanmapper.core.converter.SimpleBeanConverter;

/**
 * Converts a string into a boolean.
 *
 * @author Jeroen van Schagen
 * @since Jun 24, 2015
 */
public class StringToBooleanConverter extends SimpleBeanConverter<String, Boolean> {

    public StringToBooleanConverter() {
        super(String.class, Boolean.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Boolean doConvert(String source) {
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
