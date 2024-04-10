package io.beanmapper.utils;

public record TypeMatch<S, T>(Class<S> sourceType, Class<T> targetType) {

}
