/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core;

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
     * <br>
     * <b>This constructor requires a dependency to Spring.</b>
     */
    public AbstractBeanConverter() {
        /*
         * We only declare the full package inside the constructor to allow class loading without
         * including Spring on the classpath. But then you need to use the other constructor.
         */
        Class<?>[] types = org.springframework.core.GenericTypeResolver.resolveTypeArguments(getClass(), AbstractBeanConverter.class);
        this.sourceClass = types[0];
        this.targetClass = types[1];
    }

    /**
     * Construct a new bean converter, manually declaring the source and target class.
     * @param sourceClass the source class
     * @param targetClass the target class
     */
    public AbstractBeanConverter(Class<S> sourceClass, Class<T> targetClass) {
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
    protected abstract <R extends T> R doConvert(S source, Class<R> targetClass);

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean match(Class<?> sourceClass, Class<?> targetClass) {
        return this.sourceClass.isAssignableFrom(sourceClass) && this.targetClass.isAssignableFrom(targetClass);
    }
    
}
