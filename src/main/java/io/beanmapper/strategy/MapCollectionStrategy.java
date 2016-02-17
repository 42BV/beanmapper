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

        Collection targetItems = (Collection)getConfiguration()
                .getBeanInitializer()
                .instantiate(getConfiguration().getCollectionClass(), null);

        // When mapping a collection, a new target class must be passed. Therefore the current BeanMapper
        // is wrapped in an override configuration containing i) a reference to the target class and ii)
        // without a container class.
        BeanMapper nestedBeanMapper = getBeanMapper()
                .wrapConfig()
                .setTargetClass(getConfiguration().getTargetClass())
                .build();

        for (Object item : (Collection)source) {
            targetItems.add(nestedBeanMapper.map(item));
        }
        return targetItems;
    }

}
