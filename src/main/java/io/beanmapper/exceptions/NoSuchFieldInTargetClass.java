package io.beanmapper.exceptions;

public class NoSuchFieldInTargetClass extends RuntimeException {

    public NoSuchFieldInTargetClass(Throwable throwable, Class<?> targetClass, String fieldName) {
        super(throwable);
    }

}
