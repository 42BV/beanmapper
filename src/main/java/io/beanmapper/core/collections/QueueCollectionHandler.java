package io.beanmapper.core.collections;

import java.util.ArrayDeque;
import java.util.Queue;

import io.beanmapper.BeanMapper;

public class QueueCollectionHandler extends AbstractCollectionHandler<Queue> {

    @Override
    protected void clear(Queue target) {
        target.clear();
    }

    @Override
    protected Queue create() {
        return new ArrayDeque<>();
    }

    @Override
    public Queue copy(BeanMapper beanMapper, Class collectionElementClass, Queue source, Queue target) {
        for (var obj : source) {
            target.add(mapItem(beanMapper, collectionElementClass, obj));
        }
        return target;
    }

    @Override
    public int size(Queue targetCollection) {
        return targetCollection.size();
    }
}
