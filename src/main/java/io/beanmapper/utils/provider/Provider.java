package io.beanmapper.utils.provider;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.Map.entry;

public interface Provider<T> {


    Map<Class<?>, Provider<?>> PROVIDERS = Map.ofEntries(
        entry(boolean.class, new BooleanProvider()),
        entry(byte.class, new ByteProvider()),
        entry(short.class, new ShortProvider()),
        entry(int.class, new IntegerProvider()),
        entry(long.class, new LongProvider()),
        entry(Character.class, new CharacterProvider()),
        entry(float.class, new FloatProvider()),
        entry(double.class, new DoubleProvider()),
        entry(List.class, new ListProvider()),
        entry(Set.class, new SetProvider()),
        entry(Map.class, new MapProvider()),
        entry(Optional.class, new OprtionalProvider())
        );

    static <T> Provider<T> of(Class<T> clazz) {
        return (Provider<T>) PROVIDERS.get(clazz);
    }

    T getDefault();

    T getMaximum();
}
