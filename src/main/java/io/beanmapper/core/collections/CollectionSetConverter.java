package io.beanmapper.core.collections;

import io.beanmapper.BeanMapper;
import io.beanmapper.core.BeanFieldMatch;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class CollectionSetConverter extends AbstractCollectionConverter<Set> {

    public CollectionSetConverter(BeanMapper beanMapper) {
        super(beanMapper);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set convert(BeanFieldMatch beanFieldMatch) {
        Set sourceSet = (Set)beanFieldMatch.getSourceObject();
        Set targetSet = getTargetCollection(beanFieldMatch);

        for (Object sourceItem : sourceSet) {
            targetSet.add(convertElement(sourceItem, beanFieldMatch));
        }

        return targetSet;
    }

    @Override
    public Class<?> getCollectionClass() {
        return Set.class;
    }

    protected Set createCollection() {
        return new TreeSet();
    }

    @Override
    protected void clear(Set targetCollection) {
        targetCollection.clear();
    }

}
