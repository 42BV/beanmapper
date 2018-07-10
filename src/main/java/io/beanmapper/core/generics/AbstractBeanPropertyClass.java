package io.beanmapper.core.generics;

import java.lang.reflect.Type;

abstract class AbstractBeanPropertyClass implements BeanPropertyClass {

    public Class<?> getParameterizedType(int index) {
        if (index >= getGenericTypes().length) {
            return null;
        }
        return (Class<?>)getGenericTypes()[index];
    }

    protected abstract Type[] getGenericTypes();

}
