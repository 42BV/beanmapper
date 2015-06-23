/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.inspector;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Accessor utilities.
 *
 * @author Jeroen van Schagen
 * @since Jun 23, 2015
 */
public class PropertyAccessors {
    
    public static PropertyAccessor getProperty(Class<?> clazz, String propertyName) throws NoSuchFieldException {
        try {
            Field field = clazz.getDeclaredField(propertyName);
            return new FieldPropertyAccessor(field);
        } catch (NoSuchFieldException e) {
            if (clazz.getSuperclass() != null) {
                return getProperty(clazz.getSuperclass(), propertyName);
            } else {
                throw new NoSuchFieldException();
            }
        }
    }

    public static List<PropertyAccessor> getAll(Class<?> beanClass) {
        List<PropertyAccessor> accessors = new ArrayList<PropertyAccessor>();
        for (Field field : beanClass.getDeclaredFields()) {
            accessors.add(new FieldPropertyAccessor(field));
        }
        return accessors;
    }

}
