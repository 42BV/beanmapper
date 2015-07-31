package io.beanmapper.testmodel.collections;

import io.beanmapper.annotations.BeanCollection;

import java.util.Set;

public class CollectionSetTarget {

    @BeanCollection(elementType = Long.class)
    public Set<Long> items;

}
