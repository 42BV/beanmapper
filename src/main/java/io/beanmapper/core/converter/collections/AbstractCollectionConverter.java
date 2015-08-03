package io.beanmapper.core.converter.collections;

import io.beanmapper.BeanMapper;
import io.beanmapper.annotations.BeanCollectionUsage;
import io.beanmapper.core.BeanFieldMatch;
import io.beanmapper.core.converter.BeanConverter;
import io.beanmapper.utils.Classes;

import java.util.Collection;
import java.util.Map;

public abstract class AbstractCollectionConverter<T> implements BeanConverter {

    private final Class<?> type;
    private BeanMapper beanMapper;
    protected abstract T createCollection();

    public AbstractCollectionConverter() {
        Class<?>[] types = Classes.getParameteredTypes(getClass());
        this.type = types[0];
    }

    @Override
    public T convert(Object source, Class<?> targetClass, BeanFieldMatch beanFieldMatch) {
        T sourceCollection = (T) source;
        T targetCollection = getTargetCollection(beanFieldMatch);

        if(targetCollection instanceof Map) {
            for (Object key : ((Map)sourceCollection).keySet()) {
                ((Map) targetCollection).put(key, convertElement(((Map) sourceCollection).get(key), beanFieldMatch));
            }
        } else {
            for (Object sourceItem : (Collection)sourceCollection) {
                ((Collection) targetCollection).add(convertElement(sourceItem, beanFieldMatch));
            }
        }

        return targetCollection;
    }

    private T getTargetCollection(BeanFieldMatch beanFieldMatch) {
        BeanCollectionUsage beanCollectionUsage = beanFieldMatch.getCollectionInstructions().getBeanCollectionUsage();

        T targetCollection =
                (beanCollectionUsage == BeanCollectionUsage.CONSTRUCT || beanFieldMatch.getTargetObject() == null) ?
                        createCollection() :
                        (T)beanFieldMatch.getTargetObject();

        if (beanCollectionUsage == BeanCollectionUsage.CLEAR) {
            if(targetCollection instanceof Map)
                ((Map) targetCollection).clear();
            else
                ((Collection) targetCollection).clear();
        }

        return targetCollection;
    }

    private Object convertElement(Object source, BeanFieldMatch beanFieldMatch) {
        return beanMapper.mapForListElement(source, beanFieldMatch.getCollectionInstructions().getCollectionMapsTo());
    }

    @Override
    public boolean match(Class<?> sourceClass, Class<?> targetClass) {
        return targetClass.isAssignableFrom(type);
    }

    @Override
    public void setBeanMapper(BeanMapper beanMapper) {
        this.beanMapper = beanMapper;
    }
}
