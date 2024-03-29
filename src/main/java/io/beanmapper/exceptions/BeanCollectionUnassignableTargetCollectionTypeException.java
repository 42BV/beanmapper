package io.beanmapper.exceptions;

public class BeanCollectionUnassignableTargetCollectionTypeException extends BeanMappingException {

    public static final String ERROR = "Class %s is not assignable from %s.";

    public BeanCollectionUnassignableTargetCollectionTypeException(Class<?> staticClass, Class<?> dynamicClass) {
        super(ERROR.formatted(staticClass.getSimpleName(), dynamicClass.getSimpleName()));
    }
}
