package io.beanmapper.core.converter.impl;

import io.beanmapper.core.converter.AbstractBeanConverter;

/**
 * Created by kevinwareman on 25-06-15.
 */
public class EnumToStringConverter extends AbstractBeanConverter<Enum<?>, String> {

    @Override
    protected Object doConvert(Enum<?> source, Class<? extends String> targetClass) {
        return source.toString();
    }

}
