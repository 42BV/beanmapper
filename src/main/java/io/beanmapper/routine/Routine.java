package io.beanmapper.routine;

@FunctionalInterface
public interface Routine<S, T> {

    T apply(S source);

}
