package io.beanmapper.core.converter.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import io.beanmapper.BeanMapper;
import io.beanmapper.core.BeanPropertyMatch;
import io.beanmapper.core.converter.BeanConverter;
import io.beanmapper.utils.BeanMapperTraceLogger;
import io.beanmapper.utils.Classes;

/**
 * This converter facilitates the conversion of an arbitrary amount of Optional wrappers, however, support for complex
 * datastructures, such as Maps, Sets, List, etc. is limited to a single layer. As such, if the user requires support
 * deeper layers of complex datastructures within Optionals, they should revise their system design, and if that does
 * not solve the problem, create a custom converters for their specific conversion.
 */
public class OptionalToObjectConverter implements BeanConverter {

    /**
     * {@inheritDoc}
     */
    @Override
    public <S, T> T convert(BeanMapper beanMapper, S source, Class<T> targetClass, BeanPropertyMatch beanPropertyMatch) {
        Object obj = ((Optional<?>) source).orElse(null);

        if (targetClass.equals(Optional.class)) {
            // Not always possible to get the actual source class, so just report the name of the field. Debug log will show the call stack.
            BeanMapperTraceLogger.log("Converting Optional to Optional. Perhaps the target does not need to be an Optional?\nSource-field: {}\nTarget: {}.{}",
                    beanPropertyMatch.getSourceFieldName(),
                    beanPropertyMatch.getTarget().getClass(),
                    beanPropertyMatch.getTargetFieldName());
            return targetClass.cast(convertToOptional(beanMapper, (Optional<?>) source, beanPropertyMatch));
        } else if (obj == null) {
            return null;
        } else if (obj.getClass().equals(targetClass)) {
            return targetClass.cast(obj);
        } else if (obj instanceof Collection || obj instanceof Map<?, ?>) {
            return targetClass.cast(convertToCollection(beanMapper, obj, beanPropertyMatch));
        } else if (obj instanceof Optional<?> optional) {
            if (Collection.class.isAssignableFrom(targetClass)) {
                Class<?> genericType = (Class<?>) Classes.getParameteredTypes(beanPropertyMatch.getTarget().getClass(), beanPropertyMatch)[0];
                if (Set.class.isAssignableFrom(targetClass)) {
                    return targetClass.cast(beanMapper.map((Set<?>) optional.orElse(null), (Class<?>) genericType));
                } else {
                    return targetClass.cast(beanMapper.map((List<?>) optional.orElse(null), (Class<?>) genericType));
                }
            }
            return targetClass.cast(beanMapper.map(optional, targetClass));
        }
        return beanMapper.map(obj, targetClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean match(Class<?> sourceClass, Class<?> targetClass) {
        return sourceClass.equals(Optional.class);
    }

    @SuppressWarnings("unchecked")
    private <S, T> Optional<T> convertToOptional(BeanMapper beanMapper, Optional<S> source,
                                           BeanPropertyMatch beanPropertyMatch) {
        if (source.isEmpty()) {
            return Optional.empty();
        }

        // Get generic type of wrapping Optional
        Type genericType = Classes.getParameteredTypes(beanPropertyMatch.getTarget().getClass(), beanPropertyMatch)[0];

        // The target Optional may consist of nested Optionals. If so, we start unwrapping, until we get to the actual type.
        int numberOfWraps = 0;
        while (genericType instanceof ParameterizedType parameterizedType && parameterizedType.getRawType().equals(Optional.class)) {
            genericType = parameterizedType.getActualTypeArguments()[0];
            numberOfWraps++;
        }

        S sourceObject = source.get();

        if (genericType instanceof Class<?> targetType && Enum.class.isAssignableFrom((Class<?>) genericType) && sourceObject.getClass() == targetType) {
            return (Optional<T>) Optional.ofNullable(sourceObject);
        }

        // Place back in an Optional, as that is the target class
        Optional<?> obj = Optional.ofNullable(beanMapper.map(source.get(), (Class<?>) genericType));

        // Wrap in as many Optionals as the target requires.
        for (int index = 0; index < numberOfWraps; index++) {
            obj = Optional.of(obj);
        }
        return (Optional<T>) obj;
    }

    private Object convertToCollection(BeanMapper beanMapper, Object source, BeanPropertyMatch beanPropertyMatch) {
        if (source instanceof Collection<?> collection) {
            Type genericType = Classes.getParameteredTypes(beanPropertyMatch.getTarget().getClass(), beanPropertyMatch)[0];
            return beanMapper.map(collection, (Class<?>) genericType);
        } else if (source instanceof Map<?, ?> map) {
            Type[] genericTypes = Classes.getParameteredTypes(beanPropertyMatch.getTarget().getClass(), beanPropertyMatch);
            return new HashMap<>(beanMapper.map((Map<?, ?>) map, (Class<?>) genericTypes[1]));
        }
        return beanMapper.configuration().getDefaultValueForClass(source.getClass());
    }
}
