package io.beanmapper.shared;

import java.lang.reflect.Field;

public final class ReflectionUtils {

    public static <T> Object getValueOfField(final T instance, final Field field) {
        if (!field.canAccess(instance))
            field.setAccessible(true);
        try {
            return field.get(instance);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static <T> Field getFieldWithName(final Class<T> clazz, final String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

}
