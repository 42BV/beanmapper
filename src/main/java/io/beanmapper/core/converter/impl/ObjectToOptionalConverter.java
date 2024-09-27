package io.beanmapper.core.converter.impl;

import io.beanmapper.BeanMapper;
import io.beanmapper.core.BeanPropertyMatch;
import io.beanmapper.core.converter.BeanConverter;
import io.beanmapper.exceptions.BeanNoSuchPropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * This converter facilitates the conversion of an object to an Optional wrapping another object. This converter does
 * not support the conversion of complex datastructures, such as Collections, to Optionals. If that functionality is
 * required, please first revise your design. If that is not possible - or undesirable - create a custom converter for
 * your specific conversion.
 */
public class ObjectToOptionalConverter implements BeanConverter {

    private static final Logger log = LoggerFactory.getLogger(ObjectToOptionalConverter.class);

    @Override
    public <S, T> T convert(BeanMapper beanMapper, S source, Class<T> targetClass, BeanPropertyMatch beanPropertyMatch) {
        if (source == null) {
            return targetClass.cast(Optional.empty());
        }

        Field targetField;
        try {
            targetField = beanPropertyMatch.getTarget().getClass().getDeclaredField(beanPropertyMatch.getTargetFieldName());
        } catch (NoSuchFieldException e) {
            log.error(e.getMessage());
            throw new BeanNoSuchPropertyException(e.getMessage());
        }

        Type targetType = targetField.getGenericType();

        if (targetType instanceof ParameterizedType parameterizedType) {
            return targetClass.cast(Optional.of(beanMapper.map(source, (Class<?>) parameterizedType.getActualTypeArguments()[0])));
        } else if (targetType instanceof Class<?> type && Enum.class.isAssignableFrom(type) && source.getClass() == type) {
            return (T) source;
        }
        return targetClass.cast(Optional.of(beanMapper.map(source, (Class<?>) targetType)));
    }

    @Override
    public boolean match(Class<?> sourceClass, Class<?> targetClass) {
        return !sourceClass.equals(Optional.class) && targetClass.equals(Optional.class);
    }
}
