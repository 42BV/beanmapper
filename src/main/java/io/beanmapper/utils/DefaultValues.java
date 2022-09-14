package io.beanmapper.utils;

import java.util.Map;

public class DefaultValues {

    private static final Map<Class<?>, Object> defaultValues = Map.of(
            boolean.class, false,
            byte.class, (byte) 0,
            short.class, (short) 0,
            int.class, 0,
            char.class, '\0',
            float.class, 0.0F,
            double.class, 0.0
    );

    /**
     * Private constructor to hide implicit public constructor of utility-class.
     */
    private DefaultValues() {
    }

    @SuppressWarnings("unchecked")
    public static <T> T defaultValueFor(Class<T> clazz) {
        return (T) defaultValues.get(clazz);
    }
}