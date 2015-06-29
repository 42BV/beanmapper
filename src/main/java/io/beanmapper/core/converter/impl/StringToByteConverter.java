/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.converter.impl;

import io.beanmapper.core.converter.SimpleBeanConverter;

/**
 * Converts a string into a byte.
 *
 * @author Jeroen van Schagen
 * @since Jun 24, 2015
 */
public class StringToByteConverter extends SimpleBeanConverter<String, Byte> {

    public StringToByteConverter() {
        super(String.class, Byte.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Byte doConvert(String source) {
        return Byte.parseByte(source);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isMatchingTarget(Class<?> targetClass) {
        return super.isMatchingTarget(targetClass) || byte.class.equals(targetClass);
    }

}
