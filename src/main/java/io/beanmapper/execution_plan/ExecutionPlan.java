package io.beanmapper.execution_plan;

import java.util.HashMap;
import java.util.Map;

public class ExecutionPlan<S, T> {

    private S source;
    private T target;

    private final Map<FieldPair, ExecutionStep> executionSteps;

    public ExecutionPlan(S source, T target) {
        this.source = source;
        this.target = target;
        this.executionSteps = new HashMap<>();
    }

    public T apply() throws IllegalAccessException {
        for (var entry : executionSteps.entrySet()) {
            entry.getKey().target().set(target, entry.getValue().apply(entry.getKey().source().get(source), entry.getKey().target().getType()));
        }
        return target;
    }

}
