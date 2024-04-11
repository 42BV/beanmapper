package io.beanmapper.execution_plan;

public class FieldExecutionPlan<S, T> extends ExecutionPlan<S, T> {

    public FieldExecutionPlan(S source, T target) {
        super(source, target);
    }

}
