package io.beanmapper.execution_plan;

import java.util.function.Function;

public interface ExecutionStep<S, T> extends Function<MappingProperties<S, T>, T> {

    T apply(MappingProperties<S, T> mappingProperties);

}
