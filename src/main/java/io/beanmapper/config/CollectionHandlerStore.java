package io.beanmapper.config;

import java.util.ArrayList;
import java.util.List;

import io.beanmapper.core.collections.CollectionHandler;
import io.beanmapper.core.unproxy.BeanUnproxy;

public class CollectionHandlerStore {

    private List<CollectionHandler> collectionHandlers = new ArrayList<CollectionHandler>();

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
        for (CollectionHandler handler : getCollectionHandlers()) {
            if (handler.isMatch(clazz)) {
                return handler;
            }
        }
        // Unproxy the collection class in case it was anonymous and try again
        Class unproxiedCollectionClass = beanUnproxy.unproxy(clazz);
        for (CollectionHandler handler : getCollectionHandlers()) {
            if (handler.isMatch(unproxiedCollectionClass)) {
                return handler;
            }
        }
        return null;
    }

}
