/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.inspector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Abstraction over accessing properties.
 *
 * @author Jeroen van Schagen
 * @since Jun 23, 2015
 */
public interface PropertyAccessor {

    /**
     * Retrieve the property name.
     * @return property name
     */
    String getName();

    /**
     * Retrieve the property type.
     * @return property type
     */
    Class<?> getType();

    /**
     * Retrieve the annotation on a property.
     * @param <A> class type of the annotation class
     * @param annotationClass annotation class
     * @return the annotation, if any
     */
    <A extends Annotation> A findAnnotation(Class<A> annotationClass);

    /**
     * Determine if the property is readable.
     * @return {@code true} when readable, else {@code false}
     */
    boolean isReadable();

    /**
     * Retrieve the property value.
     * @param instance bean that contains the property
     * @return the property value
     */
    Object getValue(Object instance);

    /**
     * Determine if the property is writable.
     * @return {@code true} when writable, else {@code false}
     */
    boolean isWritable();

    /**
     * Modify the property value.
     * @param instance bean that contains the property
     * @param value the new property value
     */
    void setValue(Object instance, Object value);

    Method getReadMethod();

    Method getWriteMethod();
}
