/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.inspector;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import io.beanmapper.exceptions.BeanPropertyReadException;
import io.beanmapper.exceptions.BeanPropertyWriteException;

/**
 * Property descriptor implementation of property accessor.
 *
 * @author Jeroen van Schagen
 * @since Jun 24, 2015
 */
public class MethodPropertyAccessor implements PropertyAccessor {

    private final PropertyDescriptor descriptor;

    public MethodPropertyAccessor(PropertyDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return descriptor.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getType() {
        return descriptor.getPropertyType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <A extends Annotation> A findAnnotation(Class<A> annotationClass) {
        A annotation = null;
        if (descriptor.getReadMethod() != null) {
            annotation = descriptor.getReadMethod().getAnnotation(annotationClass);
        }
        if (descriptor.getWriteMethod() != null && annotation == null) {
            annotation = descriptor.getWriteMethod().getAnnotation(annotationClass);
        }
        return annotation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isReadable() {
        return descriptor.getReadMethod() != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getValue(Object instance) {
        if (!isReadable()) {
            throw new BeanPropertyReadException(instance.getClass(), getName());
        }

        try {
            Method readMethod = descriptor.getReadMethod();
            readMethod.setAccessible(true);
            return readMethod.invoke(instance);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new BeanPropertyReadException(instance.getClass(), getName(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWritable() {
        return descriptor.getWriteMethod() != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValue(Object instance, Object value) {
        if (!isWritable()) {
            throw new BeanPropertyWriteException(instance.getClass(), getName());
        }

        try {
            Method writeMethod = descriptor.getWriteMethod();
            writeMethod.setAccessible(true);
            writeMethod.invoke(instance, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new BeanPropertyWriteException(instance.getClass(), getName(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Method getReadMethod() {
        return descriptor.getReadMethod();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Method getWriteMethod() {
        return descriptor.getWriteMethod();
    }
}
