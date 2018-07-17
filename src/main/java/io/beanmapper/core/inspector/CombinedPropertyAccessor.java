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
    private final MethodPropertyAccessor methodAccessor;
    
    /**
     * Field based property access.
     */
    private final FieldPropertyAccessor fieldAccessor;
    
    public CombinedPropertyAccessor(PropertyDescriptor methodAccessor, Field fieldAccessor) {
        this.methodAccessor = methodAccessor != null ? new MethodPropertyAccessor(methodAccessor) : null;
        this.fieldAccessor = fieldAccessor != null ? new FieldPropertyAccessor(fieldAccessor) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return fieldAccessor != null ? fieldAccessor.getName() : methodAccessor.getName();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getType() {
        return fieldAccessor != null ? fieldAccessor.getType() : methodAccessor.getType();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public <A extends Annotation> A findAnnotation(Class<A> annotationClass) {
        A annotation = null;
        if (methodAccessor != null) {
            annotation = methodAccessor.findAnnotation(annotationClass);
        }
        if (fieldAccessor != null && annotation == null) {
            annotation = fieldAccessor.findAnnotation(annotationClass);
        }
        return annotation;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object getValue(Object instance) {
        if (isReadable(methodAccessor)) {
            return methodAccessor.getValue(instance);
        } else if (isReadable(fieldAccessor)) {
            return fieldAccessor.getValue(instance);
        }
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isReadable() {
        return isReadable(methodAccessor) || isReadable(fieldAccessor);
    }

    private boolean isReadable(PropertyAccessor accessor) {
        return accessor != null && accessor.isReadable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValue(Object instance, Object value) {
        if (isWritable(methodAccessor)) {
            methodAccessor.setValue(instance, value);
        } else if (isWritable(fieldAccessor)) {
            fieldAccessor.setValue(instance, value);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWritable() {
        return isWritable(methodAccessor) || isWritable(fieldAccessor);
    }

    private boolean isWritable(PropertyAccessor accessor) {
        return accessor != null && accessor.isWritable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Method getReadMethod() {
        return methodAccessor != null ? methodAccessor.getReadMethod() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Method getWriteMethod() {
        return methodAccessor != null ? methodAccessor.getWriteMethod() : null;
    }
}
