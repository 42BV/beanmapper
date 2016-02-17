package io.beanmapper.strategy;

import io.beanmapper.BeanMapper;
import io.beanmapper.config.Configuration;

import java.util.Collection;

@SuppressWarnings("unchecked")
public class MapCollectionStrategy extends AbstractMapStrategy {

    public MapCollectionStrategy(BeanMapper beanMapper, Configuration configuration) {
        super(beanMapper, configuration);
    }

    @Override
    public Object map(Object source) {
        Collection targetItems = (Collection)getConfiguration().getBeanInitializer().instantiate(getConfiguration().getCollectionClass(), null);
        for (Object item : (Collection)source) {
            targetItems.add(getBeanMapper().map(item, getConfiguration().getTargetClass()));
        }
        return targetItems;
    }

}
