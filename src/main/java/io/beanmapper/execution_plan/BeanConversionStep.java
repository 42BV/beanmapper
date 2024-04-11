package io.beanmapper.execution_plan;

import java.util.function.Function;

public interface BeanConversionStep<S, T> extends Function<S, T> {
}
