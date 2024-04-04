package io.beanmapper.execution_plan;

import io.beanmapper.utils.DefaultValues;

@SuppressWarnings({"rawtypes", "unchecked"})
public final class BeanConverters {

    private static final ExecutionStep NULL_VALUE_CONVERTER = mappingProperties ->
            mappingProperties.source() == null && !mappingProperties.useNullValues()
                ? DefaultValues.defaultValueFor(mappingProperties.targetClass())
                : null;

    private static final ExecutionStep<String, Long> STRING_TO_LONG_CONVERTER = mappingProperties ->  Long.parseLong(mappingProperties.source());

    private static final ExecutionStep NO_OP_CONVERTER = MappingProperties::source;

    private BeanConverters() {
        throw new AssertionError("Instance of BeanConverters-class should not be instantiated.");
    }

    public static <S, T> ExecutionStep<S, T> nullValueConverter() {
        return (ExecutionStep<S, T>) NULL_VALUE_CONVERTER;
    }

    public static ExecutionStep<String, Long> stringLongExecutionStep() {
        return STRING_TO_LONG_CONVERTER;
    }

    public static <T> ExecutionStep<T, T> targetSameTypeConverter() {
        return (ExecutionStep<T, T>) NO_OP_CONVERTER;
    }



}
