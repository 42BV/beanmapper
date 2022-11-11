package io.beanmapper.config;

import java.util.ArrayList;
import java.util.List;

import io.beanmapper.core.collections.CollectionHandler;
import io.beanmapper.core.unproxy.BeanUnproxy;

public class CollectionHandlerStore {

    private final List<CollectionHandler> collectionHandlers = new ArrayList<>();

    public List<CollectionHandler> getCollectionHandlers() {
        return collectionHandlers;
    }

    public void add(final CollectionHandler<?> collectionHandler) {
        this.collectionHandlers.add(collectionHandler);
    }

    public <C> CollectionHandler<C> getCollectionHandlerFor(final Class<?> clazz, final BeanUnproxy beanUnproxy) {
        if (clazz == null) {
            return null;
        }
        return getCollectionHandlerFor(clazz.isAnonymousClass() ? beanUnproxy.unproxy(clazz) : clazz);
    }

    private <C> CollectionHandler<C> getCollectionHandlerFor(Class<?> clazz) {
        // noinspection unchecked
        return (CollectionHandler<C>) this.collectionHandlers.stream()
                .filter(collectionHandler -> collectionHandler.isMatch(clazz))
                .findFirst()
                .orElse(null);
    }

}
