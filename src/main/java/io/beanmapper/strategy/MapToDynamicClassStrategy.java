package io.beanmapper.strategy;

import java.util.Collection;
import java.util.List;

import io.beanmapper.BeanMapper;
import io.beanmapper.config.Configuration;

public class MapToDynamicClassStrategy extends AbstractMapStrategy {

    public MapToDynamicClassStrategy(BeanMapper beanMapper, Configuration configuration) {
        super(beanMapper, configuration);
    }

    @Override
    public Object map(Object source) {
        List<String> downsizeSourceFields = getConfiguration().getDownsizeSource();
        List<String> downsizeTargetFields = getConfiguration().getDownsizeTarget();

        // If no collection class is set, but we are dealing with a collection class, make sure it is set
        Class collectionClass = getConfiguration().getCollectionClass();
        if (collectionClass == null && Collection.class.isAssignableFrom(source.getClass())) {
            getConfiguration().setCollectionClass(source.getClass());
        }

        if (downsizeSourceFields != null && downsizeSourceFields.size() > 0) {
            return downsizeSource(source, downsizeSourceFields);
        } else if (downsizeTargetFields != null && downsizeTargetFields.size() > 0) {
            return downsizeTarget(source, downsizeTargetFields);
        } else {
            // Force re-entry through the core map method, but disregard the include fields now
            return getBeanMapper()
                    .config()
                    .downsizeSource(null)
                    .downsizeTarget(null)
                    .build()
                    .map(source);
        }
    }

    public Object downsizeSource(Object source, List<String> downsizeSourceFields) {
        final Class dynamicClass = getConfiguration()
                .getClassStore()
                .getOrCreateGeneratedClass(
                        source.getClass(),
                        downsizeSourceFields,
                        getConfiguration().getStrictMappingProperties());
        Class<?> targetClass = getConfiguration().getTargetClass();
        Object target = getConfiguration().getTarget();
        Object dynSource = getBeanMapper()
                .config()
                .downsizeSource(null)
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

    public Object downsizeTarget(Object source, List<String> downsizeTargetFields) {
        final Class dynamicClass = getConfiguration().getClassStore().getOrCreateGeneratedClass(
                        getConfiguration().determineTargetClass(),
                        downsizeTargetFields,
                        getConfiguration().getStrictMappingProperties());
        return getBeanMapper()
                .config()
                .downsizeTarget(null)
                .setTargetClass(dynamicClass)
                .build()
                .map(source);
    }
}
