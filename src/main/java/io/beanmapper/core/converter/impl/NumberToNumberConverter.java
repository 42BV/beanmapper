/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.converter.impl;

import java.util.Set;

import io.beanmapper.BeanMapper;
import io.beanmapper.core.BeanPropertyMatch;
import io.beanmapper.core.converter.BeanConverter;

/**
 * Converts any number to another number type.
 *
 * @author Jeroen van Schagen
 * @since Jun 24, 2015
 */
public class NumberToNumberConverter implements BeanConverter {

    private static final Set<Class<?>> PRIMITIVES = Set.of(
            byte.class,
            short.class,
            char.class,
            int.class,
            long.class,
            float.class,
            double.class
    );

    /**
     * {@inheritDoc}
     * <br>
     * Works by first converting the number into a string and
     * then converting that string back into the target number.
     */
    @Override
    @SuppressWarnings("unchecked")
    public <S, T> T convert(BeanMapper beanMapper, S source, Class<T> targetClass, BeanPropertyMatch beanPropertyMatch) {
        if (source == null || source.getClass().equals(targetClass) || (beanPropertyMatch != null && beanPropertyMatch.getSourceClass().equals(targetClass))) {
            return (T) source;
        }
        Object sourceAsString = beanMapper
                .wrap()
                .setConverterChoosable(true)
                .setTargetClass(String.class)
                .build()
                .map(source);
        return beanMapper
                .wrap()
                .setConverterChoosable(true)
                .setTargetClass(targetClass)
                .build()
                .map(sourceAsString);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean match(Class<?> sourceClass, Class<?> targetClass) {
        return isNumber(sourceClass) && isNumber(targetClass);
    }

    private boolean isNumber(Class<?> clazz) {
        return Number.class.isAssignableFrom(clazz) || PRIMITIVES.contains(clazz);
    }

}
