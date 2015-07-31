package io.beanmapper.core.collections;

import io.beanmapper.BeanMapper;
import io.beanmapper.core.BeanFieldMatch;

import java.util.Map;
import java.util.TreeMap;

public class CollectionMapConverter extends AbstractCollectionConverter<Map> {

    public CollectionMapConverter(BeanMapper beanMapper) {
        super(beanMapper);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map convert(BeanFieldMatch beanFieldMatch) {
        Map sourceMap = (Map)beanFieldMatch.getSourceObject();
        Map targetMap = getTargetCollection(beanFieldMatch);

        for (Object key : sourceMap.keySet()) {
            targetMap.put(key, convertElement(sourceMap.get(key), beanFieldMatch));
        }

        return targetMap;
    }

    @Override
    public Class<?> getCollectionClass() {
        return Map.class;
    }

    protected Map getTargetCollection(BeanFieldMatch beanFieldMatch) {
        return beanFieldMatch.getTargetObject() != null ?
                (Map)beanFieldMatch.getTargetObject() :
                createCollection();
    }

    protected Map createCollection() {
        return new TreeMap();
    }

}
