/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.inspector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import io.beanmapper.exceptions.BeanPropertyReadException;
import io.beanmapper.exceptions.BeanPropertyWriteException;

/**
 *
 *
 * @author Jeroen van Schagen
 * @since Jun 23, 2015
 */
public class FieldPropertyAccessor implements PropertyAccessor {

    private final Field field;

    public FieldPropertyAccessor(Field field) {
        this.field = field;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return field.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getType() {
        return field.getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <A extends Annotation> A findAnnotation(Class<A> annotationClass) {
        return field.getAnnotation(annotationClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isReadable() {
        return nameOfEnum() || Modifier.isPublic(field.getModifiers());
    }

    private boolean nameOfEnum() {
        return "name".equals(field.getName()) && field.getDeclaringClass().equals(Enum.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getValue(Object instance) {
        try {
            if (instance instanceof Enum && field.getName().equals("name")) {
                return ((Enum<?>) instance).name();
            }
            if (!field.canAccess(instance)) {
                field.setAccessible(true);
            }
            return field.get(instance);
        } catch (IllegalAccessException e) {
            throw new BeanPropertyReadException(instance.getClass(), field.getName(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWritable() {
        return Modifier.isPublic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValue(Object instance, Object value) {
        try {
            if (!field.canAccess(instance)) {
                field.setAccessible(true);
            }
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new BeanPropertyWriteException(instance.getClass(), field.getName(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <A extends Annotation> boolean isAnnotationPresent(final Class<A> annotationClass) {
        return this.field.isAnnotationPresent(annotationClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Method getReadMethod() {
        throw new UnsupportedOperationException("FieldPropertyAccessor#getReadMethod is not supported. If the "
                + "read-method must be used to get the value of the field, use the MethodPropertyAccessor.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Method getWriteMethod() {
        throw new UnsupportedOperationException("FieldPropertyAccessor#getWriteMethod is not supported. If the "
                + "write-method must be used to set the value of the field, use the MethodPropertyAccessor.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S> Class<S> getDeclaringClass() {
        return (Class<S>) this.field.getDeclaringClass();
    }
}
