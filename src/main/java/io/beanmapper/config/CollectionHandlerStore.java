package io.beanmapper.config;

import java.util.ArrayList;
import java.util.List;

import io.beanmapper.core.collections.CollectionHandler;
import io.beanmapper.core.unproxy.BeanUnproxy;
import io.beanmapper.core.unproxy.UnproxyResultStore;

public class CollectionHandlerStore {

    private List<CollectionHandler> collectionHandlers = new ArrayList<>();

    public List<CollectionHandler> getCollectionHandlers() {
        return collectionHandlers;
    }

    public void add(CollectionHandler collectionHandler) {
        collectionHandlers.add(collectionHandler);
    }

    public CollectionHandler getCollectionHandlerFor(Class<?> clazz, BeanUnproxy beanUnproxy) {
        if (clazz == null) {
            return null;
        }
        // First verify if the class already has parent types which match
        CollectionHandler collectionHandler = getCollectionHandlerFor(clazz);
        if (collectionHandler != null) {
            return collectionHandler;
        }
        // Unproxy the collection class in case it was anonymous and try again
        Class<?> unproxiedClass = UnproxyResultStore.getInstance().getOrComputeUnproxyResult(clazz, beanUnproxy);
        return getCollectionHandlerFor(unproxiedClass);
    }

    private CollectionHandler getCollectionHandlerFor(Class<?> clazz) {
        for (CollectionHandler handler : getCollectionHandlers()) {
            if (handler.isMatch(clazz)) {
                return handler;
            }
        }
        return null;
    }

}
