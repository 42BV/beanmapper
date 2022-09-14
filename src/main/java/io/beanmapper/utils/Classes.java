/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Reflection utilities.
 *
 * @author Jeroen van Schagen
 * @since Jun 24, 2015
 */
public class Classes {

    /**
     * Private constructor to hide implicit public constructor of utility-class.
     */
    private Classes() {}
    
    /**
     * Retrieve the class by name.
     * @param className the class name
     * @return the class for that name
     * @throws IllegalArgumentException whenever the class name does not exist
     */
    public static Class<?> forName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Class '" + className + "' does not exist.", e);
        }
    }

    /**
     * Retrieve the parameterized types of a class.
     * @param clazz the class to check for
     * @return the parameterized types
     */
    public static Class<?>[] getParameteredTypes(Class<?> clazz) {
        ParameterizedType superClass = (ParameterizedType) clazz.getGenericSuperclass();
        Type[] types = superClass.getActualTypeArguments();
        Class<?>[] classes = new Class<?>[types.length];
        for (int index = 0; index < types.length; index++) {
            String typeName = types[index].getTypeName();
            int genericIndex = typeName.indexOf("<");
            if (genericIndex != -1) {
                typeName = typeName.substring(0, genericIndex);
            }
            classes[index] = Classes.forName(typeName);
        }
        return classes;
    }

}
