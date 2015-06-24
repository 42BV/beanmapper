/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.converter.impl;

import io.beanmapper.core.converter.AbstractBeanConverter;

import java.math.BigDecimal;

/**
 * Converts a string into a big decimal.
 *
 * @author Jeroen van Schagen
 * @since Jun 24, 2015
 */
public class StringToBigDecimalConverter extends AbstractBeanConverter<String, BigDecimal> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected BigDecimal doConvert(String source, Class<? extends BigDecimal> targetClass) {
        return new BigDecimal(source);
    }

}
