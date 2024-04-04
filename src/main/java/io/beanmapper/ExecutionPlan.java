package io.beanmapper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class ExecutionPlan<S, T> implements BiFunction<S, T, T> {

    private final List<ExecutionStep> executionSteps;

    public ExecutionPlan() {
        this.executionSteps = new ArrayList<>();
    }

    @Override
    public T apply(S s, T t) {
        return t;
    }
}
