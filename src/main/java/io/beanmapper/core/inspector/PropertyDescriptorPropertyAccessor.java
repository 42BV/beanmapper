/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.inspector;

import io.beanmapper.exceptions.BeanGetFieldException;
import io.beanmapper.exceptions.BeanSetFieldException;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Property descriptor implementation of property accessor.
 *
 * @author Jeroen van Schagen
 * @since Jun 24, 2015
 */
public class PropertyDescriptorPropertyAccessor implements PropertyAccessor {
    
    private final PropertyDescriptor descriptor;

    public PropertyDescriptorPropertyAccessor(PropertyDescriptor descriptor) {
        this.descriptor = descriptor;
        makeAccessable(descriptor.getReadMethod());
        makeAccessable(descriptor.getWriteMethod());
    }
    
    private void makeAccessable(Method method) {
        if (method != null) {
            method.setAccessible(true);
        }
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
            throw new BeanGetFieldException(instance.getClass(), getName());
        }

        try {
            return descriptor.getReadMethod().invoke(instance);
        } catch (IllegalAccessException e) {
            throw new BeanGetFieldException(instance.getClass(), getName(), e);
        } catch (InvocationTargetException e) {
            throw new BeanGetFieldException(instance.getClass(), getName(), e);
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
            throw new BeanSetFieldException(instance.getClass(), getName());
        }
        
        try {
            descriptor.getWriteMethod().invoke(instance, value);
        } catch (IllegalAccessException e) {
            throw new BeanSetFieldException(instance.getClass(), getName(), e);
        } catch (InvocationTargetException e) {
            throw new BeanSetFieldException(instance.getClass(), getName(), e);
        }
    }

}
