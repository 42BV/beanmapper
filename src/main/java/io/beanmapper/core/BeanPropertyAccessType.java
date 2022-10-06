package io.beanmapper.core;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.beanmapper.core.inspector.PropertyAccessor;

public enum BeanPropertyAccessType {

    FIELD {
        @Override
        public <T> Type getGenericType(Class<T> containingClass, PropertyAccessor accessor) {
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
        public <T> Type getGenericType(Class<T> containingClass, PropertyAccessor accessor) {
            return accessor.getWriteMethod().getGenericParameterTypes()[0];
        }
    },
    GETTER {
        @Override
        public <T> Type getGenericType(Class<T> containingClass, PropertyAccessor accessor) {
            return accessor.getReadMethod().getGenericReturnType();
        }
    },
    NO_ACCESS {
        @Override
        public <T> ParameterizedType getGenericType(Class<T> containingClass, PropertyAccessor accessor) {
            throw new BeanPropertyAccessException("The field " + accessor.getName() + " cannot be accessed");
        }
    };

    private static <T> Class<? super T> getFirstClassContainingField(final Class<T> currentClass, final String fieldName) {
        Field[] allFields = currentClass.getDeclaredFields();
        Class<? super T> unproxied = currentClass;
        while (!containsField(fieldName, allFields)) {
            unproxied = unproxied.getSuperclass();
            if (unproxied == null || Object.class.equals(currentClass)) {
                return null;
            }
            allFields = unproxied.getDeclaredFields();
        }
        return unproxied;
    }

    private static boolean containsField(String fieldName, Field[] fields) {
        for (Field field : fields) {
            if (field.getName().equals(fieldName)) {
                return true;
            }
        }
        return false;
    }

    public abstract <T> Type getGenericType(Class<T> containingClass, PropertyAccessor accessor);

}
