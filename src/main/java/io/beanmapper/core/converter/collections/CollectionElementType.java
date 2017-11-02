package io.beanmapper.core.converter.collections;

public class CollectionElementType {

    private final Class<?> collectionElementType;

    private final boolean derived;

    private CollectionElementType(Class<?> collectionElementType, boolean derived) {
        this.collectionElementType = collectionElementType;
        this.derived = derived;
    }

    public static CollectionElementType set(Class<?> collectionElementType) {
        return new CollectionElementType(collectionElementType, false);
    }

    public static CollectionElementType derived(Class<?> collectionElementType) {
        return new CollectionElementType(collectionElementType, true);
    }

    public boolean isDerived() {
        return derived;
    }

    public Class<?> getType() {
        return collectionElementType;
    }

}
