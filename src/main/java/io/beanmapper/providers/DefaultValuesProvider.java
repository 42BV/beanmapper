package io.beanmapper.providers;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.Map.entry;
import static java.util.Objects.isNull;

public class DefaultValuesProvider implements Provider {
    private static final Map<Class<?>, Object> DEFAULT_VALUES = Map.ofEntries(
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

    @Override
    public <T extends S, S> T get(Class<S> source) {
        if (isNull(source)) return null;
        return (T) DEFAULT_VALUES.get(source);
    }
}