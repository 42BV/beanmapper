package io.beanmapper.execution_plan;

public interface ExecutionStep {

    <S, T> T apply(S source, T target);

    <S, T> T apply(S source, Class<T> targetClass);

}
