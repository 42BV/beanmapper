package io.beanmapper.core.converter.impl;

import java.util.Optional;

import io.beanmapper.BeanMapper;
import io.beanmapper.core.BeanPropertyMatch;
import io.beanmapper.core.converter.BeanConverter;

public class OptionalToObjectConverter implements BeanConverter {

    @Override
    public <S, T> T convert(BeanMapper beanMapper, S source, Class<T> targetClass, BeanPropertyMatch beanPropertyMatch) {
        Object obj = ((Optional<?>) source).orElse(null);
        if (obj != null && obj.getClass().equals(targetClass)) {
            return (T) obj;
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

}
