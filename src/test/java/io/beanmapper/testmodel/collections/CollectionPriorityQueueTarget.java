package io.beanmapper.testmodel.collections;

import java.util.PriorityQueue;
import java.util.Queue;

import io.beanmapper.annotations.BeanCollection;
import io.beanmapper.annotations.BeanCollectionUsage;

public class CollectionPriorityQueueTarget {

    @BeanCollection(elementType = Long.class, preferredCollectionClass = PriorityQueue.class, beanCollectionUsage = BeanCollectionUsage.CONSTRUCT)
    public Queue<Long> queue;

}
