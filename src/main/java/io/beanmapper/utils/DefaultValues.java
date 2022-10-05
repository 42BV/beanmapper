package io.beanmapper.utils;

import static java.util.Map.entry;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class DefaultValues {

    private static final Map<Class<?>, Object> defaultValues = Map.ofEntries(
            entry(boolean.class, false),
            entry(byte.class, (byte) 0),
            entry(short.class, (short) 0),
            entry(int.class, 0),
            entry(long.class, 0L),
            entry(char.class, '\0'),
            entry(float.class, 0.0F),
            entry(double.class, 0.0),
            entry(Optional.class, Optional.empty()),
            entry(List.class, Collections.emptyList()),
            entry(Set.class, Collections.emptySet()),
            entry(Map.class, Collections.emptyMap())
    );

    /**
     * Private constructor to hide implicit public constructor of utility-class.
     */
    private DefaultValues() {
    }

    @SuppressWarnings("unchecked")
    public static <T, V> V defaultValueFor(Class<T> clazz) {
        return (V) defaultValues.get(clazz);
    }
}