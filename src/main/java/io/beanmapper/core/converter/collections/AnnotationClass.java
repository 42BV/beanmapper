package io.beanmapper.core.converter.collections;

public record AnnotationClass(Class<?> annotationClass) {

    public static final AnnotationClass EMPTY_ANNOTATION_CLASS = new AnnotationClass(null);

    public AnnotationClass(Class<?> annotationClass) {
        this.annotationClass = determineClass(annotationClass);
    }

    private static Class<?> determineClass(Class<?> collectionElementType) {
        return collectionElementType == null || collectionElementType.equals(void.class) ?
                null :
                collectionElementType;
    }

    public Class<?> getAnnotationClass() {
        return this.annotationClass;
    }

    public boolean isEmpty() {
        return this.annotationClass == null;
    }

}
