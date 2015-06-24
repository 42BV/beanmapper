/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.converter;

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

    /**
     * Construct a new bean converter, dynamically resolving the source and target class. 
     */
    public AbstractBeanConverter() {
        Class<?>[] types = Classes.getParameteredTypes(getClass());
        this.sourceClass = types[0];
        this.targetClass = types[1];
    }

    /**
     * Construct a new bean converter, manually declaring the source and target class.
     * @param sourceClass the source class
     * @param targetClass the target class
     */
    public AbstractBeanConverter(Class<?> sourceClass, Class<?> targetClass) {
        this.sourceClass = sourceClass;
        this.targetClass = targetClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public final Object convert(Object source, Class<?> targetClass) {
        if (source == null) {
            return null;
        }
        if (!sourceClass.isAssignableFrom(source.getClass())) {
            throw new IllegalArgumentException("Expected an instance of: " + sourceClass.getName());
        }
        if (!this.targetClass.isAssignableFrom(targetClass)) {
            throw new IllegalArgumentException("Cannot only convert to: " + targetClass.getName());
        }
        return doConvert((S) source, (Class<T>) targetClass);
    }
    
    /**
     * Convert a source instance to the target type.
     * @param source the source instance
     * @return the converted source instance
     */
    protected abstract Object doConvert(S source, Class<? extends T> targetClass);

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean match(Class<?> sourceClass, Class<?> targetClass) {
        return this.sourceClass.isAssignableFrom(sourceClass) && this.targetClass.isAssignableFrom(targetClass);
    }
    
}
