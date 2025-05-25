package io.beanmapper.core.converter.collections;

public record CollectionElementType(AnnotationClass collectionElementType, boolean derived) {

    public static final CollectionElementType EMPTY_COLLECTION_ELEMENT_TYPE =
            new CollectionElementType(new AnnotationClass(null), true);

    private CollectionElementType(Class<?> collectionElementType, boolean derived) {
        this(new AnnotationClass(collectionElementType), derived);
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
