/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.converter.impl;

import java.util.HashMap;
import java.util.Map;

import io.beanmapper.BeanMapper;
import io.beanmapper.core.BeanPropertyMatch;
import io.beanmapper.core.converter.BeanConverter;
import io.beanmapper.utils.Check;

/**
 * Converter between boxed and primitive type. 
 *
 * @author Jeroen van Schagen
 * @since Jun 24, 2015
 */
public class PrimitiveConverter implements BeanConverter {

    private static final Map<Class<?>, Class<?>> MAPPINGS = new HashMap<Class<?>, Class<?>>();

    static {
        register(Byte.class, byte.class);
        register(Short.class, short.class);
        register(Integer.class, int.class);
        register(Long.class, long.class);
        register(Double.class, double.class);
        register(Float.class, float.class);
        register(Boolean.class, boolean.class);
    }

    private static void register(Class<?> boxedClass, Class<?> primitiveClass) {
        MAPPINGS.put(boxedClass, primitiveClass);
        MAPPINGS.put(primitiveClass, boxedClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S, T> T convert(BeanMapper beanMapper, S source, Class<T> targetClass, BeanPropertyMatch beanPropertyMatch) {
        return source != null ? (T) source : beanMapper.getConfiguration().getDefaultValueForClass(targetClass); // Value will automatically be boxed or unboxed
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean match(Class<?> sourceClass, Class<?> targetClass) {
        return targetClass.equals(MAPPINGS.get(sourceClass));
    }

}
