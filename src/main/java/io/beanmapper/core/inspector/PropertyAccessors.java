/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.inspector;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Accessor utilities.
 *
 * @author Jeroen van Schagen
 * @since Jun 23, 2015
 */
public class PropertyAccessors {

    private static final String CLASS_PROPERTY = "class";

    /**
     * Private constructor to hide the implicit public constructor of utility-class.
     */
    private PropertyAccessors() {
    }

    /**
     * Retrieve all property accessors that relate to a bean.
     * @param beanClass the bean class
     * @return the property accessors of that bean
     */
    public static List<PropertyAccessor> getAll(Class<?> beanClass) {
        Map<String, PropertyDescriptor> descriptors = beanClass.isRecord() ?
                findPropertyDescriptorsForRecord((Class<? extends Record>) beanClass) :
                findPropertyDescriptors(beanClass);
        Map<String, Field> fields = findAllFields(beanClass);

        Set<String> propertyNames = new HashSet<>();
        propertyNames.addAll(descriptors.keySet());
        propertyNames.addAll(fields.keySet());

        List<PropertyAccessor> accessors = new ArrayList<>();
        for (String propertyName : propertyNames) {
            PropertyDescriptor descriptor = descriptors.get(propertyName);
            Field field = fields.get(propertyName);
            accessors.add(new CombinedPropertyAccessor(descriptor, field));
        }
        return accessors;
    }

    /**
     * Compiles a Map&lt;String, PropertyDescriptor&gt;, by generating a new PropertyDescriptor for every
     * RecordComponent.
     *
     * <p>Utilises the {@link PropertyDescriptor#PropertyDescriptor(String, Method, Method)
     * PropertyDescriptor(String, Method, Method)}-constructor, passing null for the write-method.</p>
     *
     * @param clazz The Record-class, for which the PropertyDescriptors must be retrieved.
     * @return The Map of PropertyDescriptors.
     */
    private static Map<String, PropertyDescriptor> findPropertyDescriptorsForRecord(Class<? extends Record> clazz) {
        var recordComponents = clazz.getRecordComponents();
        Map<String, PropertyDescriptor> result = new HashMap<>();
        try {
            for (var component : recordComponents) {
                result.put(component.getName(), new PropertyDescriptor(component.getName(), component.getAccessor(), null));
            }
        } catch (IntrospectionException ex) {
            throw new IllegalStateException("Could not introspect record: " + clazz.getSimpleName());
        }
        result.remove(CLASS_PROPERTY);
        return result;
    }

    private static Map<String, PropertyDescriptor> findPropertyDescriptors(Class<?> clazz) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
            Map<String, PropertyDescriptor> result = new HashMap<>();
            for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
                result.put(descriptor.getName(), descriptor);
            }
            result.remove(CLASS_PROPERTY);
            if (clazz.isInterface()) {
                addParentInterfaceProperties(clazz, result);
            }
            return result;
        } catch (IntrospectionException e) {
            throw new IllegalStateException("Could not introspect bean: " + clazz.getSimpleName());
        }
    }

    private static void addParentInterfaceProperties(Class<?> clazz, Map<String, PropertyDescriptor> result) {
        for (Class<?> parent : clazz.getInterfaces()) {
            result.putAll(findPropertyDescriptors(parent));
        }
    }

    private static Map<String, Field> findAllFields(Class<?> clazz) {
        Map<String, Field> fields = new HashMap<>();
        for (Field field : clazz.getDeclaredFields()) {
            fields.put(field.getName(), field);
        }
        if (clazz.getSuperclass() != null) {
            fields.putAll(findAllFields(clazz.getSuperclass()));
        }
        return fields;
    }

    /**
     * Retrieve a specific property accessor.
     * @param beanClass the bean class
     * @param propertyName the property name
     * @return the accessor that can manage the property
     */
    public static PropertyAccessor findProperty(Class<?> beanClass, String propertyName) {
        PropertyAccessor result = null;
        PropertyDescriptor descriptor = findPropertyDescriptor(beanClass, propertyName);
        Field field = findField(beanClass, propertyName);
        if (descriptor != null || field != null) {
            result = new CombinedPropertyAccessor(descriptor, field);
        }
        return result;
    }

    private static PropertyDescriptor findPropertyDescriptor(Class<?> beanClass, String propertyName) {
        Map<String, PropertyDescriptor> descriptors = beanClass.isRecord()
                ? findPropertyDescriptorsForRecord((Class<? extends Record>) beanClass)
                : findPropertyDescriptors(beanClass);
        return descriptors.get(propertyName);
    }

    private static Field findField(Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            if (clazz.getSuperclass() != null) {
                return findField(clazz.getSuperclass(), fieldName);
            } else {
                return null;
            }
        }
    }

}
