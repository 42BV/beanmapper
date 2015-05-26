package io.beanmapper.core;

/**
 * This class can be inherited if you want to add your own converter to the beanMapper.
 * You must supply the types of both the source and target class.
 * After instantiation of the beanMapper, you can add the converter module by
 * calling the addConverter() method.
 */
public interface BeanConverter<S, T> {

    T convert(S source);

    Class<S> getSourceClass();

    Class<T> getTargetClass();

    boolean match(Class sourceClass, Class targetClass);

}