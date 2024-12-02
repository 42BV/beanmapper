package io.beanmapper.strategy;

import java.util.Collection;
import java.util.List;

import io.beanmapper.BeanMapper;
import io.beanmapper.config.Configuration;
import io.beanmapper.utils.BeanMapperPerformanceLogger;

public class MapToDynamicClassStrategy extends AbstractMapStrategy {

    private static final String LOGGING_STRING = "Recursively calling BeanMapper#map(Object), to map source of type %s, to type %s.";

    public MapToDynamicClassStrategy(BeanMapper beanMapper, Configuration configuration) {
        super(beanMapper, configuration);
    }

    @Override
    public <S, T> T map(S source) {
        List<String> downsizeSourceFields = getConfiguration().getDownsizeSource();
        List<String> downsizeTargetFields = getConfiguration().getDownsizeTarget();

        // If no collection class is set, but we are dealing with a collection class, make sure it is set
        Class<?> collectionClass = getConfiguration().getCollectionClass();
        if (collectionClass == null && Collection.class.isAssignableFrom(source.getClass())) {
            getConfiguration().setCollectionClass(source.getClass());
        }

        if (downsizeSourceFields != null && !downsizeSourceFields.isEmpty()) {
            return downsizeSource(source, downsizeSourceFields);
        } else if (downsizeTargetFields != null && !downsizeTargetFields.isEmpty()) {
            return downsizeTarget(source, downsizeTargetFields);
        } else {
            // Force re-entry through the core map method, but disregard the include-fields now
            return getBeanMapper()
                    .wrap()
                    .downsizeSource(null)
                    .downsizeTarget(null)
                    .build()
                    .map(source);
        }
    }

    public <S, T> T downsizeSource(S source, List<String> downsizeSourceFields) {
        final Class<?> dynamicClass = getConfiguration()
                .getClassStore()
                .getOrCreateGeneratedClass(
                        source.getClass(),
                        downsizeSourceFields,
                        getConfiguration().getStrictMappingProperties());
        Class<?> targetClass = getConfiguration().getTargetClass();
        Object target = getConfiguration().getTarget();

        Object dynSource = BeanMapperPerformanceLogger.runTimed(() -> getBeanMapper()
                        .wrap()
                        .downsizeSource(null)
                        .setTarget(target)
                        .setTargetClass(dynamicClass)
                        .build()
                        .map(source), LOGGING_STRING, source.getClass().getSimpleName(), targetClass != null ? targetClass.getSimpleName() : null);

        return BeanMapperPerformanceLogger.runTimed(() -> getBeanMapper()
                        .wrap()
                        .downsizeSource(null)
                        .setTarget(target)
                        .setTargetClass(targetClass)
                        .build()
                        .map(dynSource), LOGGING_STRING, dynSource.getClass().getSimpleName(), targetClass != null ? targetClass.getSimpleName() : null);
    }

    public <S, T> T downsizeTarget(S source, List<String> downsizeTargetFields) {
        final Class<?> dynamicClass = getConfiguration().getClassStore().getOrCreateGeneratedClass(
                getConfiguration().determineTargetClass(),
                downsizeTargetFields,
                getConfiguration().getStrictMappingProperties());
        Class<?> collectionClass = getBeanMapper().configuration().getCollectionClass();
        return BeanMapperPerformanceLogger.runTimed(() -> getBeanMapper()
                .wrap()
                .downsizeTarget(null)
                .setCollectionClass(collectionClass)
                .setTargetClass(dynamicClass)
                .build()
                .map(source), LOGGING_STRING, source.getClass().getSimpleName(), dynamicClass.getSimpleName());
    }
}
