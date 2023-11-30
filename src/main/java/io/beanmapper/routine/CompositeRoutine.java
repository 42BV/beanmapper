package io.beanmapper.routine;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class CompositeRoutine<S, T> implements Routine<S, T> {

    private final S source;
    private final Map<Field, Routine<?, ?>> internalRoutines;

    public CompositeRoutine(S source) {
        this.source = source;
        this.internalRoutines = new HashMap<>();
    }

    @Override
    public T apply(S source) {
        Map<Field, Object> results = new HashMap<>();
        this.internalRoutines.forEach((key, value) -> results.put(key, value.apply(this.getValue(key))));
        // Map values into target
        return null;
    }

    public <U, V> CompositeRoutine<S, T> withRoutine(Field sourceField, Routine<U, V> routine) {
        this.internalRoutines.put(sourceField, routine);
        return this;
    }

    private <V> V getValue(Field field) {
        try {
            return (V) field.get(this.source);
        } catch (IllegalAccessException | IllegalArgumentException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
