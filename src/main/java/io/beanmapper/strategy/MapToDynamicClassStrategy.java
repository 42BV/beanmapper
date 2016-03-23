package io.beanmapper.strategy;

import io.beanmapper.BeanMapper;
import io.beanmapper.config.Configuration;

import java.util.Collection;
import java.util.List;

public class MapToDynamicClassStrategy extends AbstractMapStrategy {

    public MapToDynamicClassStrategy(BeanMapper beanMapper, Configuration configuration) {
        super(beanMapper, configuration);
    }

    @Override
    public Object map(Object source) {
        List<String> limitSourceFields = getConfiguration().getDownsizeSource();
        List<String> limitTargetFields = getConfiguration().getDownsizeTarget();

        // If no collection class is set, but we are dealing with a collection class, make sure it is set
        Class collectionClass = getConfiguration().getCollectionClass();
        if (collectionClass == null && Collection.class.isAssignableFrom(source.getClass())) {
            getConfiguration().setCollectionClass(source.getClass());
        }

        if (limitSourceFields != null && limitSourceFields.size() > 0) {
            return limitSource(source, limitSourceFields);
        } else if (limitTargetFields != null && limitTargetFields.size() > 0) {
            return limitTarget(source, limitTargetFields);
        } else {
            // Force re-entry through the core map method, but disregard the include fields now
            return getBeanMapper()
                    .config()
                    .limitSource(null)
                    .limitTarget(null)
                    .build()
                    .map(source);
        }
    }

    public Object limitSource(Object source, List<String> limitSourceFields) {
        final Class dynamicClass = getConfiguration().getClassStore().getOrCreateGeneratedClass(source.getClass(), limitSourceFields);
        Class<?> targetClass = getConfiguration().getTargetClass();
        Object target = getConfiguration().getTarget();
        Object dynSource = getBeanMapper()
                .config()
                .limitSource(null)
                .setTargetClass(dynamicClass)
                .build()
                .map(source);


        return getBeanMapper()
                .wrapConfig()
                .setTargetClass(targetClass)
                .setTarget(target)
                .build()
                .map(dynSource);
    }

    public Object limitTarget(Object source, List<String> limitTargetFields) {
        final Class dynamicClass = getConfiguration().getClassStore().getOrCreateGeneratedClass(getConfiguration().determineTargetClass(), limitTargetFields);
        return getBeanMapper()
                .config()
                .limitTarget(null)
                .setTargetClass(dynamicClass)
                .build()
                .map(source);
    }
}
