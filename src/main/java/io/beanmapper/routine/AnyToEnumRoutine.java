package io.beanmapper.routine;

public final class AnyToEnumRoutine<S, T extends Enum<T>> implements Routine<S, T> {

    private final Class<T> clazz;

    public AnyToEnumRoutine(Class<T> target) {
        this.clazz = target;
    }

    @Override
    public T apply(S source) {
        return Enum.valueOf(this.clazz, source.toString());
    }
}
