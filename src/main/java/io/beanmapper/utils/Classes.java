/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import io.beanmapper.core.BeanPropertyMatch;
import io.beanmapper.core.unproxy.BeanUnproxy;
import io.beanmapper.core.unproxy.DefaultBeanUnproxy;
import io.beanmapper.core.unproxy.SkippingBeanUnproxy;
import io.beanmapper.exceptions.BeanNoSuchPropertyException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reflection utilities.
 *
 * @author Jeroen van Schagen
 * @since Jun 24, 2015
 */
public class Classes {

    private static final Logger log = LoggerFactory.getLogger(Classes.class);

    private static final BeanUnproxy unproxy = new SkippingBeanUnproxy(new DefaultBeanUnproxy());

    /**
     * Private constructor to hide implicit public constructor of utility-class.
     */
    private Classes() {
    }

    /**
     * Retrieve the class by name.
     *
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
     *
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

    public static Type[] getParameteredTypes(Class<?> clazz, BeanPropertyMatch beanPropertyMatch) {
        try {
            return ((ParameterizedType) clazz.getDeclaredField(beanPropertyMatch.getTargetFieldName())
                    .getGenericType()).getActualTypeArguments();
        } catch (NoSuchFieldException e) {
            log.error(e.getMessage());
            throw new BeanNoSuchPropertyException(e.getMessage());
        }
    }

    public static <S extends Collection<?>, E1> Class<E1> getSourceElementClass(S source) {
        // Find the common superclass or interface from the elements in a list
        List<Class<E1>> elementClasses = new ArrayList<>();
        if (source.size() == 1) {
            return (Class<E1>) unproxy.unproxy(source.iterator().next().getClass());
        }
        for (var element : source) {
            elementClasses.add((Class<E1>) unproxy.unproxy(element.getClass()));
        }
        Class<?> commonSuperclass = findCommonSuperclass(elementClasses);
        return (Class<E1>) commonSuperclass;
    }

    public static <S extends Map<?, ?>, E> Class<E> getSourceElementClass(S source) {
        return getSourceElementClass(source.values());
    }

    // Create findCommonSuperclass-method
    private static <E> Class<E> findCommonSuperclass(List<Class<E>> classes) {
        if (classes.isEmpty()) {
            return (Class<E>) Object.class;
        }
        Class<E> commonSuperclass = classes.get(0);
        for (int i = 1; i < classes.size(); i++) {
            commonSuperclass = findCommonSuperclass(commonSuperclass, classes.get(i));
        }
        return commonSuperclass;
    }

    // Create findCommonSuperclass-overload, which takes two classes as parameters
    private static <E> Class<E> findCommonSuperclass(Class<E> class1, Class<E> class2) {
        if (class1.equals(class2)) {
            return class1;
        }
        if (class1.isAssignableFrom(class2)) {
            return class1;
        }
        if (class2.isAssignableFrom(class1)) {
            return class2;
        }
        Class<?> superClass = class1.getSuperclass();
        while (superClass != null && !superClass.equals(Object.class)) {
            if (superClass.isAssignableFrom(class2)) {
                return (Class<E>) superClass;
            }
        }
        return (Class<E>) Object.class;
    }

}
