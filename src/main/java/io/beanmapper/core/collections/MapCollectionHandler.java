package io.beanmapper.core.collections;

import java.util.Map;
import java.util.TreeMap;

import io.beanmapper.BeanMapper;

public class MapCollectionHandler extends AbstractCollectionHandler<Map> {

    @Override
    public Map copy(BeanMapper beanMapper, Class collectionElementClass, Map source, Map target) {
        for (Object key : source.keySet()) {
            target.put(
                    key,
                    mapItem(beanMapper, collectionElementClass, source.get(key)));
        }
        return target;
    }

    @Override
    public int size(Map targetCollection) {
        return targetCollection.size();
    }

    @Override
    protected void clear(Map target) {
        target.clear();
    }

    @Override
    protected Map create() {
        return new TreeMap();
    }

}
