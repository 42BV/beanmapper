package io.beanmapper.core.collections;

import io.beanmapper.BeanMapper;
import io.beanmapper.core.BeanFieldMatch;

import java.util.ArrayList;
import java.util.List;

public class CollectionListConverter extends AbstractCollectionConverter<List> {

    public CollectionListConverter(BeanMapper beanMapper) {
        super(beanMapper);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List convert(BeanFieldMatch beanFieldMatch) {
        List sourceList = (List)beanFieldMatch.getSourceObject();
        List targetList = getTargetCollection(beanFieldMatch);

        for (Object sourceItem : sourceList) {
            targetList.add(convertElement(sourceItem, beanFieldMatch));
        }

        return targetList;
    }

    @Override
    public Class<?> getCollectionClass() {
        return List.class;
    }

    protected List createCollection() {
        return new ArrayList();
    }

    @Override
    protected void clear(List targetCollection) {
        targetCollection.clear();
    }

}
