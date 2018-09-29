package io.beanmapper.core;

import io.beanmapper.config.CollectionHandlerStore;
import io.beanmapper.core.unproxy.BeanUnproxy;

public interface BeanMatchStoreFactory {
    BeanMatchStore create(CollectionHandlerStore collectionHandlerStore, BeanUnproxy beanUnproxy);
}
