package io.beanmapper.utils;

import static io.beanmapper.core.converter.collections.CollectionElementType.derived;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

import io.beanmapper.core.collections.CollectionHandler;
import io.beanmapper.core.converter.collections.CollectionElementType;

public class GenericTypeDeterminer {

    private final CollectionHandler collectionHandler;
    private final Class<?> containingClass;
    private final String fieldName;

    public GenericTypeDeterminer(CollectionHandler collectionHandler, Class<?> containingClass, String fieldName) {
        this.collectionHandler = collectionHandler;
        this.containingClass = containingClass;
        this.fieldName = fieldName;
    }

    public CollectionElementType determineGenericType() {
        try {
            Class<?> classWithField = getFirstClassContainingField(containingClass, fieldName);
            if (classWithField == null) {
                return null;
            }
            ParameterizedType type = (ParameterizedType)classWithField.getDeclaredField(fieldName).getGenericType();
            return derived(collectionHandler.determineGenericParameterFromType(type));
        } catch (Exception err) {
            return null;
        }
    }

    private Class getFirstClassContainingField(Class<?> currentClass, String fieldName) {
        Field[] allFields = currentClass.getDeclaredFields();
        while (!containsField(fieldName, allFields)) {
            currentClass = currentClass.getSuperclass();
            if (Object.class.equals(currentClass)) {
                return null;
            }
            allFields = currentClass.getDeclaredFields();
        }
        return currentClass;
    }

    private boolean containsField(String fieldName, Field[] fields) {
        for (Field field : fields) {
            if (field.getName().equals(fieldName)) {
                return true;
            }
        }
        return false;
    }

}
