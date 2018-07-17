package io.beanmapper.core.generics;

public interface BeanPropertyClass {

    Class<?> getBasicType();

    Class<?> getParameterizedType(int index);

}
