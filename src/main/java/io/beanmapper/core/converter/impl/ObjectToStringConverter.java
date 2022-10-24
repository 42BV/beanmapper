/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.converter.impl;

import io.beanmapper.core.converter.SimpleBeanConverter;

/**
 * Converts objects into strings.
 *
 * @author Jeroen van Schagen
 * @since Jun 24, 2015
 */
public class ObjectToStringConverter extends SimpleBeanConverter<Object, String> {

    public ObjectToStringConverter() {
        super(Object.class, String.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String doConvert(Object source) {
        return source.toString();
    }

}
