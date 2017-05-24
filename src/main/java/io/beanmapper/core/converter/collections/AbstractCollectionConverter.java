package io.beanmapper.core.converter.collections;

import java.util.Collection;
import java.util.Map;

import io.beanmapper.BeanMapper;
import io.beanmapper.annotations.BeanCollectionUsage;
import io.beanmapper.core.BeanFieldMatch;
import io.beanmapper.core.constructor.DefaultBeanInitializer;
import io.beanmapper.core.converter.BeanConverter;
import io.beanmapper.exceptions.BeanCollectionUnassignableTargetCollectionTypeException;
import io.beanmapper.utils.Classes;

public abstract class AbstractCollectionConverter<T> implements BeanConverter {

    private final Class<?> type;

    private final DefaultBeanInitializer beanInitializer;

    public AbstractCollectionConverter() {
        Class<?>[] types = Classes.getParameteredTypes(getClass());
        this.type = types[0];
        this.beanInitializer = new DefaultBeanInitializer();
    }

    @Override
    public T convert(BeanMapper beanMapper, Object source, Class<?> targetClass, BeanFieldMatch beanFieldMatch) {
        T sourceCollection = (T) source;

        if (beanFieldMatch.getCollectionInstructions() == null) {
            return sourceCollection;
        }

        T targetCollection = getTargetCollection(beanFieldMatch);

        if(targetCollection instanceof Map) {
            for (Object key : ((Map)sourceCollection).keySet()) {
                ((Map) targetCollection).put(key, convertElement(beanMapper, ((Map) sourceCollection).get(key), beanFieldMatch));
            }
        } else {
            for (Object sourceItem : (Collection)sourceCollection) {
                ((Collection) targetCollection).add(convertElement(beanMapper, sourceItem, beanFieldMatch));
            }
        }

        return targetCollection;
    }

    private T getTargetCollection(BeanFieldMatch beanFieldMatch) {

        BeanCollectionUsage beanCollectionUsage = beanFieldMatch.getCollectionInstructions().getBeanCollectionUsage();

        T targetCollection =
                (beanCollectionUsage == BeanCollectionUsage.CONSTRUCT || beanFieldMatch.getTargetObject() == null) ?
                        createCollection(beanFieldMatch.getCollectionInstructions().getTargetCollectionType()) :
                        (T)beanFieldMatch.getTargetObject();

        if (beanCollectionUsage == BeanCollectionUsage.CLEAR) {
            if(targetCollection instanceof Map)
                ((Map) targetCollection).clear();
            else
                ((Collection) targetCollection).clear();
        }

        return targetCollection;
    }

    private T createCollection(Class targetCollectionType) {
        if (targetCollectionType == null) {
            return createCollection();
        } else if (!type.isAssignableFrom(targetCollectionType)) {
            throw new BeanCollectionUnassignableTargetCollectionTypeException(type, targetCollectionType);
        }

        return (T) beanInitializer.instantiate(targetCollectionType, null);
    }

    protected abstract T createCollection();

    private Object convertElement(BeanMapper beanMapper, Object source, BeanFieldMatch beanFieldMatch) {
        return beanMapper.map(source, beanFieldMatch.getCollectionInstructions().getCollectionMapsTo(), true);
    }

    @Override
    public boolean match(Class<?> sourceClass, Class<?> targetClass) {
        return targetClass.isAssignableFrom(type);
    }


}
