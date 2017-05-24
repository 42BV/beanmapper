package io.beanmapper.core.converter.collections;

import io.beanmapper.annotations.BeanCollectionUsage;

public class BeanCollectionInstructions {

    private Class collectionMapsTo;

    private Class<?> targetCollectionType;

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

    public Class<?> getTargetCollectionType() {
        return targetCollectionType;
    }

    public void setTargetCollectionType(Class<?> targetCollectionType) {
        this.targetCollectionType = targetCollectionType;
    }
}
