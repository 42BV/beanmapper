package io.beanmapper.core.converter.collections;

import io.beanmapper.BeanMapper;
import io.beanmapper.core.BeanFieldMatch;
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
            BeanFieldMatch beanFieldMatch) {

        T sourceCollection = (T) source;

        if (beanFieldMatch.getCollectionInstructions() == null) {
            return sourceCollection;
        }

        return (T)beanMapper.wrap()
                .setCollectionClass(collectionHandler.getType())
                .setCollectionUsage(beanFieldMatch.getCollectionInstructions().getBeanCollectionUsage())
                .setPreferredCollectionClass(beanFieldMatch.getCollectionInstructions().getPreferredCollectionClass().getAnnotationClass())
                .setFlushAfterClear(beanFieldMatch.getCollectionInstructions().getFlushAfterClear())
                .setTargetClass(beanFieldMatch.getCollectionInstructions().getCollectionElementType().getType())
                .setTarget(beanFieldMatch.getTargetObject())
                .build()
                .map(sourceCollection);
    }

    @Override
    public boolean match(Class<?> sourceClass, Class<?> targetClass) {
        return collectionHandler.isMatch(targetClass);
    }

}
