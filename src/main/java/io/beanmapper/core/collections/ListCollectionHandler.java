package io.beanmapper.core.collections;

import java.util.ArrayList;
import java.util.List;

import io.beanmapper.BeanMapper;

public class ListCollectionHandler extends AbstractCollectionHandler<List> {

    @Override
    public List copy(BeanMapper beanMapper, Class collectionElementClass, List source, List target) {
        for (Object item : source) {
            target.add(mapItem(beanMapper, collectionElementClass, item));
        }
        return target;
    }

    @Override
    public int size(List targetCollection) {
        return targetCollection.size();
    }

    @Override
    protected void clear(List target) {
        target.clear();
    }

    @Override
    protected List create() {
        return new ArrayList<>();
    }

}
