/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.inspector;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Property accessor that looks at both the getter/setter
 * methods and also at fields.
 *
 * @author Jeroen van Schagen
 * @since Jun 24, 2015
 */
public class CombinedPropertyAccessor implements PropertyAccessor {
    
    /**
     * Method based property access.
     */
    private final PropertyDescriptorPropertyAccessor descriptor;
    
    /**
     * Field based property access.
     */
    private final FieldPropertyAccessor field;
    
    public CombinedPropertyAccessor(PropertyDescriptor descriptor, Field field) {
        this.descriptor = descriptor != null ? new PropertyDescriptorPropertyAccessor(descriptor) : null;
        this.field = field != null ? new FieldPropertyAccessor(field) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return field != null ? field.getName() : descriptor.getName();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getType() {
        return field != null ? field.getType() : descriptor.getType();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public <A extends Annotation> A findAnnotation(Class<A> annotationClass) {
        A annotation = null;
        if (descriptor != null) {
            annotation = descriptor.findAnnotation(annotationClass);
        }
        if (field != null && annotation == null) {
            annotation = field.findAnnotation(annotationClass);
        }
        return annotation;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object getValue(Object instance) {
        if (isReadable(descriptor)) {
            return descriptor.getValue(instance);
        } else if (isReadable(field)) {
            return field.getValue(instance);
        }
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isReadable() {
        return isReadable(descriptor) || isReadable(field);
    }

    private boolean isReadable(PropertyAccessor accessor) {
        return accessor != null && accessor.isReadable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValue(Object instance, Object value) {
        if (isWritable(descriptor)) {
            descriptor.setValue(instance, value);
        } else if (isWritable(field)) {
            field.setValue(instance, value);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWritable() {
        return isWritable(descriptor) || isWritable(field);
    }

    private boolean isWritable(PropertyAccessor accessor) {
        return accessor != null && accessor.isWritable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Method getReadMethod() {
        return descriptor != null ? descriptor.getReadMethod() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Method getWriteMethod() {
        return descriptor != null ? descriptor.getWriteMethod() : null;
    }
}
