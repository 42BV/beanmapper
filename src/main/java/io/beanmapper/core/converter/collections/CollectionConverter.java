package io.beanmapper.core.converter.collections;

import io.beanmapper.BeanMapper;
import io.beanmapper.core.BeanPropertyMatch;
import io.beanmapper.core.collections.CollectionHandler;
import io.beanmapper.core.converter.BeanConverter;

public class CollectionConverter<T> implements BeanConverter {

    private final CollectionHandler<T> collectionHandler;

    public CollectionConverter(CollectionHandler<T> collectionHandler) {
        this.collectionHandler = collectionHandler;
    }

    @Override
    public T convert(
            BeanMapper beanMapper,
            Object source,
            Class<?> targetClass,
            BeanPropertyMatch beanPropertyMatch) {

        T sourceCollection = (T) source;

        if (beanPropertyMatch.getCollectionInstructions() == null) {
            return sourceCollection;
        }

        return (T) beanMapper.wrap()
                .setCollectionClass(collectionHandler.getType())
                .setCollectionUsage(beanPropertyMatch.getCollectionInstructions().getBeanCollectionUsage())
                .setPreferredCollectionClass(beanPropertyMatch.getCollectionInstructions().getPreferredCollectionClass().getAnnotationClass())
                .setFlushAfterClear(beanPropertyMatch.getCollectionInstructions().getFlushAfterClear())
                .setTargetClass(beanPropertyMatch.getCollectionInstructions().getCollectionElementType().getType())
                .setTarget(beanPropertyMatch.getTargetObject())
                .setUseNullValue()
                .build()
                .map(sourceCollection);
    }

    @Override
    public boolean match(Class<?> sourceClass, Class<?> targetClass) {
        return collectionHandler.isMatch(targetClass);
    }

}
