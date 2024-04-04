package io.beanmapper.execution_plan;

public record MappingProperties<S, T>(S source, T target, Class<T> targetClass, boolean useNullValues) {
}
