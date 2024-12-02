package io.beanmapper.core.converter.collections;

import io.beanmapper.BeanMapper;
import io.beanmapper.core.BeanPropertyMatch;
import io.beanmapper.core.collections.CollectionHandler;
import io.beanmapper.core.converter.BeanConverter;
import io.beanmapper.utils.BeanMapperPerformanceLogger;

public class CollectionConverter implements BeanConverter {

    private static final String LOGGING_STRING = "%s#convert(BeanMapper, Object, Class, BeanPropertyMatch) -> BeanMapper#map(Object)";

    private final CollectionHandler<?> collectionHandler;

    public CollectionConverter(CollectionHandler<?> collectionHandler) {
        this.collectionHandler = collectionHandler;
    }

    @Override
    public <R, U> U convert(
            BeanMapper beanMapper,
            R source,
            Class<U> targetClass,
            BeanPropertyMatch beanPropertyMatch) {

        if (beanPropertyMatch == null || beanPropertyMatch.getCollectionInstructions() == null) {
            return targetClass.cast(source);
        }

        return BeanMapperPerformanceLogger.runTimed(() -> beanMapper.wrap()
                .setCollectionClass(collectionHandler.getType())
                .setCollectionUsage(beanPropertyMatch.getCollectionInstructions().getBeanCollectionUsage())
                .setPreferredCollectionClass(beanPropertyMatch.getCollectionInstructions().getPreferredCollectionClass().annotationClass())
                .setFlushAfterClear(beanPropertyMatch.getCollectionInstructions().getFlushAfterClear())
                .setTargetClass(beanPropertyMatch.getCollectionInstructions().getCollectionElementType().getType())
                .setTarget(beanPropertyMatch.getTargetObject())
                .setUseNullValue()
                .build()
                .map(source), LOGGING_STRING, getClass().getSimpleName());
    }

    @Override
    public boolean match(Class<?> sourceClass, Class<?> targetClass) {
        return collectionHandler.isMatch(targetClass);
    }

}
