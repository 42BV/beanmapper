package io.beanmapper.core.converter.collections;

import io.beanmapper.BeanMapper;
import io.beanmapper.core.BeanPropertyMatch;
import io.beanmapper.core.collections.CollectionHandler;
import io.beanmapper.core.converter.BeanConverter;

public class CollectionConverter implements BeanConverter {

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

        return beanMapper.wrap()
                .setCollectionClass(collectionHandler.getType())
                .setCollectionUsage(beanPropertyMatch.getCollectionInstructions().getBeanCollectionUsage())
                .setPreferredCollectionClass(beanPropertyMatch.getCollectionInstructions().getPreferredCollectionClass().getAnnotationClass())
                .setFlushAfterClear(beanPropertyMatch.getCollectionInstructions().getFlushAfterClear())
                .setTargetClass(beanPropertyMatch.getCollectionInstructions().getCollectionElementType().getType())
                .setTarget(beanPropertyMatch.getTargetObject())
                .setUseNullValue()
                .build()
                .map(source);
    }

    @Override
    public boolean match(Class<?> sourceClass, Class<?> targetClass) {
        return collectionHandler.isMatch(targetClass);
    }

}
