package io.beanmapper.core.collections;

import io.beanmapper.annotations.BeanCollectionUsage;

public class BeanCollectionInstructions {

    private Class collectionMapsTo;

    private BeanCollectionUsage beanCollectionUsage;

    public Class getCollectionMapsTo() {
        return collectionMapsTo;
    }

    public void setCollectionMapsTo(Class collectionMapsTo) {
        this.collectionMapsTo = collectionMapsTo;
    }

    public BeanCollectionUsage getBeanCollectionUsage() {
        return beanCollectionUsage;
    }

    public void setBeanCollectionUsage(BeanCollectionUsage beanCollectionUsage) {
        this.beanCollectionUsage = beanCollectionUsage;
    }

}
