package io.beanmapper.core.converter.collections;

public class CollectionElementType {

    public static final CollectionElementType EMPTY_COLLECTION_ELEMENT_TYPE =
            new CollectionElementType(null, true);

    private final AnnotationClass classOfCollectionElement;

    private final boolean derived;

    private CollectionElementType(Class<?> classOfCollectionElement, boolean derived) {
        this.classOfCollectionElement = new AnnotationClass(classOfCollectionElement);
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
        return classOfCollectionElement.annotationClass();
    }

    public boolean isEmpty() {
        return classOfCollectionElement.isEmpty();
    }

}
