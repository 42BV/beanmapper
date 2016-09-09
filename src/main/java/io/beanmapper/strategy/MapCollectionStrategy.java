package io.beanmapper.strategy;

import java.util.Collection;

import io.beanmapper.BeanMapper;
import io.beanmapper.config.Configuration;

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
        Class targetClass = getConfiguration().getTargetClass();
        logger.debug("    [");
        for (Object item : (Collection)source) {
            BeanMapper nestedBeanMapper = getBeanMapper()
                    .wrapConfig()
                    .setTargetClass(targetClass)
                    .build();
            targetItems.add(nestedBeanMapper.map(item));
        }
        logger.debug("    ]");

        return targetItems;
    }

}
