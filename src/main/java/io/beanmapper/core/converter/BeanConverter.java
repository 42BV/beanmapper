package io.beanmapper.core.converter;

import io.beanmapper.BeanMapper;
import io.beanmapper.core.BeanPropertyMatch;

/**
 * This class can be inherited if you want to add your own converter to the beanMapper.
 * You must supply the types of both the source and target class.
 * After instantiation of the beanMapper, you can add the converter module by
 * calling the addConverter() method.
 */
public interface BeanConverter {

    /**
     * Converts the source instance into the desired target type.
     * @param beanMapper the instance of BeanMapper to use for further mappings
     * @param source the source instance
     * @param targetClass the desired target type
     * @param beanPropertyMatch information on the field pair (source / target)
     * @return the converted source instance
     */
    <S, T> T convert(BeanMapper beanMapper, S source, Class<T> targetClass, BeanPropertyMatch beanPropertyMatch);

    /**
     * Determines if the conversion of our source type to a 
     * target type is supported by this converter.
     * @param sourceClass the source class
     * @param targetClass the target class
     * @return {@code true} if the conversion is supported, else {@code false}
     */
    boolean match(Class<?> sourceClass, Class<?> targetClass);

}
