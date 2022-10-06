package io.beanmapper.exceptions;

public class BeanCollectionNotSupportedException extends BeanMappingException {

    public static final String ERROR = "The collection type combination %s | %s is not supported";

    public BeanCollectionNotSupportedException(Class<?> sourceClass, Class<?> targetClass) {
        super(ERROR.formatted(sourceClass.getSimpleName(), targetClass.getSimpleName()));
    }

}
