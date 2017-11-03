package io.beanmapper.core.converter.collections;

public class AnnotationClass {

    public static final AnnotationClass EMPTY_ANNOTATION_CLASS = new AnnotationClass(null);

    private final Class<?> annotationClass;

    public AnnotationClass(Class<?> annotationClass) {
        this.annotationClass = determineClass(annotationClass);
    }

    private Class<?> determineClass(Class<?> collectionElementType) {
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
