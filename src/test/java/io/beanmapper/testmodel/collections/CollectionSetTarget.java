package io.beanmapper.testmodel.collections;

import java.util.Set;

import io.beanmapper.annotations.BeanCollection;

public class CollectionSetTarget {

    @BeanCollection(elementType = Long.class)
    public Set<Long> items;

}
