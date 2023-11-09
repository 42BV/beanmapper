package io.beanmapper.utils;

public class CanonicalClassName {
    public static String determineCanonicalClassName(Class<?> clazz) {
        return clazz.getCanonicalName() == null ?
                fallbackWhenCanonicalNameIsNull(clazz) :
                clazz.getCanonicalName();
    }

    // See bug 190, https://github.com/42BV/beanmapper/issues/190
    // When an enum has abstract methods, calling getCanonicalName() on its class will return a null value.
    // This fallback method will allow for the expected canonical name value to be returned. getCanonicalName
    // must no longer be called directly.
    private static String fallbackWhenCanonicalNameIsNull(Class<?> clazz) {
        return clazz.getSuperclass().getCanonicalName();
    }
}
