package io.beanmapper.core;

public abstract class AbstractBeanConverter<S,T> implements BeanConverter<S, T> {

    @Override
    public boolean match(Class sourceClass, Class targetClass) {
        return  getSourceClass().equals(sourceClass) &&
                getTargetClass().equals(targetClass);
    }

}
