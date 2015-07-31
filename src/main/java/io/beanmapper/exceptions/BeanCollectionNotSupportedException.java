package io.beanmapper.exceptions;

public class BeanCollectionNotSupportedException extends BeanMappingException {

    public static final String ERROR = "The collection type combination %s | %s is not supported";

    public BeanCollectionNotSupportedException(Class sourceClass, Class targetClass) {
        super(String.format(ERROR, sourceClass.getSimpleName(), targetClass.getSimpleName()));
    }

}
