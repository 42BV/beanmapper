package io.beanmapper.testmodel.collections;

import java.util.LinkedList;
import java.util.Set;

import io.beanmapper.annotations.BeanCollection;

public class CollectionSetTargetIncorrectSubtype {

    /*
     * LinkedList is not a subtype of Set
     * so an error will occur.
     */
    @BeanCollection(elementType = Long.class, targetCollectionType = LinkedList.class)
    public Set<Long> items;
}
