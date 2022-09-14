/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.converter;

import io.beanmapper.BeanMapper;
import io.beanmapper.core.BeanPropertyMatch;
import io.beanmapper.utils.Check;
import io.beanmapper.utils.Classes;

/**
 * Template implementation of a single type converter.
 *
 * @author Jeroen van Schagen
 * @since Jun 18, 2015
 */
public abstract class AbstractBeanConverter<S, T> implements BeanConverter {
    
    private final Class<?> sourceClass;
    
    private final Class<?> targetClass;

    protected BeanMapper beanMapper;

    /**
     * Construct a new bean converter, dynamically resolving the source and target class. 
     */
    protected AbstractBeanConverter() {
        Class<?>[] types = Classes.getParameteredTypes(getClass());
        this.sourceClass = types[0];
        this.targetClass = types[1];
    }

    /**
     * Construct a new bean converter, manually declaring the source and target class.
     * @param sourceClass the source class
     * @param targetClass the target class
     */
    protected AbstractBeanConverter(Class<?> sourceClass, Class<?> targetClass) {
        this.sourceClass = sourceClass;
        this.targetClass = targetClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public final Object convert(BeanMapper beanMapper, Object source, Class<?> targetClass, BeanPropertyMatch beanPropertyMatch) {
        this.beanMapper = beanMapper;
        if (source == null) {
            Check.argument(!targetClass.isPrimitive(), "Cannot convert null into primitive.");
            return null;
        }
        Check.argument(isMatchingSource(source.getClass()), "Unsupported source class.");
        Check.argument(isMatchingTarget(targetClass), "Unsupported target class.");
        return doConvert((S) source, (Class<T>) targetClass);
    }
    
    /**
     * Convert a source instance to the target type.
     * @param source the source instance
     * @param targetClass the class type to convert to
     * @return the converted source instance
     */
    protected abstract T doConvert(S source, Class<? extends T> targetClass);

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean match(Class<?> sourceClass, Class<?> targetClass) {
        return isMatchingSource(sourceClass) && isMatchingTarget(targetClass);
    }
    
    protected boolean isMatchingSource(Class<?> sourceClass) {
        return this.sourceClass.isAssignableFrom(sourceClass);
    }
    
    protected boolean isMatchingTarget(Class<?> targetClass) {
        return this.targetClass.isAssignableFrom(targetClass);
    }
}
