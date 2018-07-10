package io.beanmapper.core;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.beanmapper.core.inspector.PropertyAccessor;

public enum BeanPropertyAccessType {

    FIELD {
        @Override
        public Type getGenericType(Class containingClass, PropertyAccessor accessor) {
            String fieldName = accessor.getName();
            Class<?> classWithField = getFirstClassContainingField(containingClass, fieldName);
            if (classWithField == null) {
                return null;
            }
            try {
                return classWithField.getDeclaredField(fieldName).getGenericType();
            } catch (NoSuchFieldException e) {
                throw new BeanPropertyAccessException(e, "The generic type of " + classWithField + "." + fieldName + " cannot be accessed");
            }
        }

    },
    SETTER {
        @Override
        public Type getGenericType(Class containingClass, PropertyAccessor accessor) {
            return accessor.getWriteMethod().getGenericParameterTypes()[0];
        }
    },
    GETTER {
        @Override
        public Type getGenericType(Class containingClass, PropertyAccessor accessor) {
            return accessor.getReadMethod().getGenericReturnType();
        }
    },
    NO_ACCESS {
        @Override
        public ParameterizedType getGenericType(Class containingClass, PropertyAccessor accessor) {
            throw new BeanPropertyAccessException("The field " + accessor.getName() + " cannot be accessed");
        }
    };

    public abstract Type getGenericType(Class containingClass, PropertyAccessor accessor);

    private static Class getFirstClassContainingField(Class<?> currentClass, String fieldName) {
        Field[] allFields = currentClass.getDeclaredFields();
        while (!containsField(fieldName, allFields)) {
            currentClass = currentClass.getSuperclass();
            if (currentClass == null || Object.class.equals(currentClass)) {
                return null;
            }
            allFields = currentClass.getDeclaredFields();
        }
        return currentClass;
    }

    private static boolean containsField(String fieldName, Field[] fields) {
        for (Field field : fields) {
            if (field.getName().equals(fieldName)) {
                return true;
            }
        }
        return false;
    }

}
