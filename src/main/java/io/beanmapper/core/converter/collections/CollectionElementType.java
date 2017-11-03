package io.beanmapper.core.converter.collections;

public class CollectionElementType {

    public final static CollectionElementType EMPTY_COLLECTION_ELEMENT_TYPE =
            new CollectionElementType(null, true);

    private final AnnotationClass collectionElementType;

    private final boolean derived;

    private CollectionElementType(Class<?> collectionElementType, boolean derived) {
        this.collectionElementType = new AnnotationClass(collectionElementType);
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
        return collectionElementType.getAnnotationClass();
    }

    public boolean isEmpty() {
        return collectionElementType.isEmpty();
    }

}
