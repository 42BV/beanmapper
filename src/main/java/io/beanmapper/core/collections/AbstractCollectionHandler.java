package io.beanmapper.core.collections;

import io.beanmapper.BeanMapper;
import io.beanmapper.annotations.BeanCollectionUsage;
import io.beanmapper.config.CollectionFlusher;
import io.beanmapper.core.constructor.DefaultBeanInitializer;
import io.beanmapper.exceptions.BeanCollectionUnassignableTargetCollectionTypeException;
import io.beanmapper.utils.Classes;

public abstract class AbstractCollectionHandler<C> implements CollectionHandler<C> {

    private final Class<C> type;
    private final DefaultBeanInitializer beanInitializer = new DefaultBeanInitializer();

    protected AbstractCollectionHandler() {
        this.type = (Class<C>)Classes.getParameteredTypes(getClass())[0];
    }

    /**
     * Calls the clear method on the target collection
     * @param target the collection to call clear() on
     */
    protected abstract void clear(C target);

    /**
     * Creates a new instance of the collection class
     * @return new instance of the collection class
     */
    protected abstract C create();

    public Object mapItem(
            BeanMapper beanMapper,
            Class<?> collectionElementClass,
            Object source) {

        return beanMapper
                .wrap()
                .setTargetClass(collectionElementClass)
                .setCollectionClass(null)
                .setConverterChoosable(true)
                .build()
                .map(source);
    }

    @Override
    public C getTargetCollection(
            BeanCollectionUsage collectionUsage,
            Class<C> preferredCollectionClass,
            Class<?> collectionElementClass,
            C targetCollection,
            CollectionFlusher collectionFlusher,
            boolean mustFlush) {

        C useTargetCollection = collectionUsage.mustConstruct(targetCollection) ?
                createCollection(preferredCollectionClass, collectionElementClass) :
                targetCollection;

        if (collectionUsage.mustClear() && size(useTargetCollection) > 0) {
            clear(useTargetCollection);
            collectionFlusher.flush(mustFlush);
        }

        return useTargetCollection;
    }

    @Override
    public Class<C> getType() {
        return type;
    }

    private C createCollection(Class<C> preferredCollectionClass, Class<?> collectionElementClass) {
        if (preferredCollectionClass == null) {
            return create(collectionElementClass);
        } else if (!type.isAssignableFrom(preferredCollectionClass)) {
            throw new BeanCollectionUnassignableTargetCollectionTypeException(type, preferredCollectionClass);
        }
        return beanInitializer.instantiate(preferredCollectionClass, null);
    }

    @Override
    public boolean isMatch(Class<?> clazz) {
        return getType().isAssignableFrom(clazz);
    }

    protected C create(Class<?> elementClass) {
        return create();
    }

    public int getGenericParameterIndex() {
        return 0;
    }

}