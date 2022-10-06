package io.beanmapper.strategy;

import io.beanmapper.BeanMapper;
import io.beanmapper.config.Configuration;
import io.beanmapper.core.BeanMatch;

public class MapToInstanceStrategy extends AbstractMapStrategy {

    public MapToInstanceStrategy(BeanMapper beanMapper, Configuration configuration) {
        super(beanMapper, configuration);
    }

    @Override
    public <S, T> T map(S source) {
        return (T) map(source, getConfiguration().getTarget());
    }

    protected <S, T> T map(S source, T target) {
        return map(source, target, getBeanMatch(source.getClass(), target.getClass()));
    }

    protected <S, T> T map(S source, T target, BeanMatch beanMatch) {
        return processProperties(source, target, beanMatch);
    }
}
