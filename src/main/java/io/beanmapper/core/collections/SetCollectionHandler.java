package io.beanmapper.core.collections;

import java.util.Set;
import java.util.TreeSet;

import io.beanmapper.BeanMapper;

public class SetCollectionHandler extends AbstractCollectionHandler<Set> {

    @Override
    public Set copy(BeanMapper beanMapper, Class collectionElementClass, Set source, Set target) {
        for (Object item : source) {
            target.add(mapItem(beanMapper, collectionElementClass, item));
        }
        return target;
    }

    @Override
    public int size(Set targetCollection) {
        return targetCollection.size();
    }

    @Override
    protected void clear(Set target) {
        target.clear();
    }

    @Override
    protected Set create() {
        return new TreeSet();
    }

}
