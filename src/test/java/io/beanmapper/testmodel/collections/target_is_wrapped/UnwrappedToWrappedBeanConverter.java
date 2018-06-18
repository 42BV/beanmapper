package io.beanmapper.testmodel.collections.target_is_wrapped;

import io.beanmapper.BeanMapper;
import io.beanmapper.core.BeanFieldMatch;
import io.beanmapper.core.converter.BeanConverter;

public class UnwrappedToWrappedBeanConverter implements BeanConverter {

    @Override
    public Object convert(BeanMapper beanMapper, Object source, Class<?> targetClass, BeanFieldMatch beanFieldMatch) {
        UnwrappedSource unwrappedSource = (UnwrappedSource)source;
        return new WrappedTarget(unwrappedSource);
    }

    @Override
    public boolean match(Class<?> sourceClass, Class<?> targetClass) {
        return sourceClass.equals(UnwrappedSource.class) &&
                targetClass.equals(WrappedTarget.class);
    }

}
