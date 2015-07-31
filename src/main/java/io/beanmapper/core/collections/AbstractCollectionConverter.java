package io.beanmapper.core.collections;

import io.beanmapper.BeanMapper;
import io.beanmapper.annotations.BeanCollectionUsage;
import io.beanmapper.core.BeanFieldMatch;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCollectionConverter<T> implements CollectionConverter<T> {

    protected BeanMapper beanMapper;

    public AbstractCollectionConverter(BeanMapper beanMapper) {
        this.beanMapper = beanMapper;
    }

    public abstract T convert(BeanFieldMatch beanFieldMatch);

    protected T getTargetCollection(BeanFieldMatch beanFieldMatch) {
        BeanCollectionUsage beanCollectionUsage = beanFieldMatch.getCollectionInstructions().getBeanCollectionUsage();

        T targetCollection =
                (beanCollectionUsage == BeanCollectionUsage.CONSTRUCT ||
                beanFieldMatch.getTargetObject() == null) ?
                createCollection() :
                (T)beanFieldMatch.getTargetObject();

        if (beanCollectionUsage == BeanCollectionUsage.CLEAR) {
            clear(targetCollection);
        }

        return targetCollection;
    }

    protected abstract T createCollection();

    protected abstract void clear(T targetCollection);

    protected Object convertElement(Object source, BeanFieldMatch beanFieldMatch) {
        return beanMapper.mapForListElement(source, beanFieldMatch.getCollectionInstructions().getCollectionMapsTo());
    }
}
