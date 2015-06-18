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
     * 
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
    public final <R> R convert(Object source, Class<R> targetClass) {
        if (source == null) {
            return null;
        }
        if (!source.getClass().equals(sourceClass)) {
            throw new IllegalArgumentException("Expected an instance of: " + sourceClass.getName());
        }
        if (!this.targetClass.equals(targetClass)) {
            throw new IllegalArgumentException("Cannot only convert to: " + targetClass.getName());
        }
        return (R) convert((S) source);
    }
    
    /**
     * Convert a source instance to the target type.
     * @param source the source instance
     * @return the converted source instance
     */
    public abstract T convert(S source);

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean match(Class<?> sourceClass, Class<?> targetClass) {
        return sourceClass.equals(this.sourceClass) && targetClass.equals(this.targetClass);
    }
    
}
