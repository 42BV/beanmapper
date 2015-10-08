package io.beanmapper.utils;

import java.util.HashMap;
import java.util.Map;

public class DefaultValues {

    static final Map<Class<?>,Object> defaultValues = new HashMap<Class<?>,Object>()
    {{
            put(boolean.class, false);
            put(byte.class, (byte) 0);
            put(short.class, (short) 0);
            put(int.class, 0);
            put(long.class, 0L);
            put(char.class, '\0');
            put(float.class, 0.0F);
            put(double.class, 0.0);
    }};

    @SuppressWarnings("unchecked")
    public static<T> T defaultValueFor(Class<T> clazz) {
        return (T)defaultValues.get(clazz);
    }
}