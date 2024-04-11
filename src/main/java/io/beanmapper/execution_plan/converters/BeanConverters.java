package io.beanmapper.execution_plan.converters;

import io.beanmapper.execution_plan.BeanConversionStep;
import io.beanmapper.execution_plan.MappingProperties;

@SuppressWarnings({"unchecked"})
public final class BeanConverters {

    private static final BeanConversionStep<?, ?> NULL_VALUE_CONVERTER = source -> source;

    private static final BeanConversionStep<String, Long> STRING_TO_LONG_CONVERTER = Long::parseLong;

    private static final BeanConversionStep<?, ?> NO_OP_CONVERTER = source -> source;

    private static final BeanConversionStep<?, Enum<?>> ANY_TO_ENUM = new AnyToEnumConverter();

    private BeanConverters() {
        throw new AssertionError("Instance of BeanConverters-class should not be instantiated.");
    }

    public static <S, T> BeanConversionStep<S, T> nullValueConverter() {
        return (BeanConversionStep<S, T>) NULL_VALUE_CONVERTER;
    }

    public static BeanConversionStep<String, Long> stringLongExecutionStep() {
        return STRING_TO_LONG_CONVERTER;
    }

    public static <T> BeanConversionStep<T, T> targetSameTypeConverter() {
        return (BeanConversionStep<T, T>) NO_OP_CONVERTER;
    }

    public static <S, T extends Enum<T>> BeanConversionStep<S, T> anyToEnumConverter() {
        return (BeanConversionStep<S, T>) ANY_TO_ENUM;
    }

    private static final class AnyToEnumConverter<T extends Enum<T>> implements BeanConversionStep<MappingProperties, Enum<T>> {

        private boolean isNotEmpty(String name) {
            return name != null && !name.trim().isEmpty();
        }

        private T valueOf(Class<T> enumClass, String name) {
            return Enum.valueOf(enumClass, name);
        }

        @Override
        public Enum<T> apply(MappingProperties o) {
            String sourceText = o.source() instanceof Enum<?> enumerable ? enumerable.name() : o.toString();
            if (isNotEmpty(sourceText)) {
                return valueOf((Class<T>) o.targetClass(), sourceText);
            }
            return null;
        }
    }

}
