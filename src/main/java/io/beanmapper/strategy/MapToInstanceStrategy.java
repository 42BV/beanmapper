package io.beanmapper.strategy;

import io.beanmapper.BeanMapper;
import io.beanmapper.config.Configuration;
import io.beanmapper.core.BeanMatch;

public class MapToInstanceStrategy extends AbstractMapStrategy {

    public MapToInstanceStrategy(BeanMapper beanMapper, Configuration configuration) {
        super(beanMapper, configuration);
    }

    @Override
    public Object map(Object source) {
        return map(source, getConfiguration().getTarget());
    }

    protected Object map(Object source, Object target) {
        return map(source, target, getBeanMatch(source.getClass(), target.getClass()));
    }

    protected Object map(Object source, Object target, BeanMatch beanMatch) {
        return processProperties(source, target, beanMatch);
    }
}
