package io.beanmapper.core;

import java.util.Objects;

/**
 * This class can be inherited if you want to add your own converter to the beanMapper.
 * You must supply the types of both the source and target class.
 * After instantiation of the beanMapper, you can add the converter module by
 * calling the addConverter() method.
 */
public abstract class BeanConverter<S, T> {

    public abstract T from(S classFrom);

    public abstract S to(T classTo);

}
