package io.beanmapper.execution_plan;

import java.util.HashMap;
import java.util.Map;

public class ExecutionPlan<S, T> implements ExecutionStep<S, T> {

    private final MappingProperties<S, T> mappingProperties;
    private final Map<FieldMatch, ExecutionStep> fieldExecutionSteps = new HashMap<>();

    public void addFieldConverter(FieldMatch field, ExecutionStep executionStep) {
        fieldExecutionSteps.put(field, executionStep);
    }

    public ExecutionPlan(S source, T target) {
        this.mappingProperties = new MappingProperties<>(source, target, (Class<T>) target.getClass(), true);
    }

    @Override
    public T apply(MappingProperties<S, T> mappingProperties) {
        return null;
    }
}
