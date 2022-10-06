package io.beanmapper.core.generics;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

abstract class AbstractBeanPropertyClass implements BeanPropertyClass {

    public Class<?> getParameterizedType(int index) {
        if (index >= getGenericTypes().length) {
            return null;
        }
        return convertToClass(getGenericTypes()[index]);
    }

    public Class<?> convertToClass(Type type) {
        if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getRawType();
        } else {
            return (Class<?>) type;
        }
    }

    protected abstract Type[] getGenericTypes();

}
