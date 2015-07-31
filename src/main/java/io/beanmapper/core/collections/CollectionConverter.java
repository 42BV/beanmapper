package io.beanmapper.core.collections;

import io.beanmapper.core.BeanFieldMatch;

public interface CollectionConverter<T> {

    T convert(BeanFieldMatch beanFieldMatch);

    Class<?> getCollectionClass();
}
