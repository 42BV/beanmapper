/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.converter.impl;

import io.beanmapper.core.converter.SimpleBeanConverter;

import java.math.BigDecimal;

/**
 * Converts a string into a big decimal.
 *
 * @author Jeroen van Schagen
 * @since Jun 24, 2015
 */
public class StringToBigDecimalConverter extends SimpleBeanConverter<String, BigDecimal> {

    public StringToBigDecimalConverter() {
        super(String.class, BigDecimal.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BigDecimal doConvert(String source) {
        return new BigDecimal(source);
    }

}
