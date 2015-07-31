package io.beanmapper.core.collections;

import io.beanmapper.BeanMapper;
import io.beanmapper.core.BeanFieldMatch;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCollectionConverter<T> implements CollectionConverter<T> {

    protected BeanMapper beanMapper;

    public AbstractCollectionConverter(BeanMapper beanMapper) {
        this.beanMapper = beanMapper;
    }

    public abstract T convert(BeanFieldMatch beanFieldMatch);

    protected  abstract T getTargetCollection(BeanFieldMatch beanFieldMatch);

    protected abstract T createCollection();

    protected Object convertElement(Object source, BeanFieldMatch beanFieldMatch) {
        return beanMapper.mapForListElement(source, beanFieldMatch.getCollectionInstructions().getCollectionMapsTo());
    }
}
