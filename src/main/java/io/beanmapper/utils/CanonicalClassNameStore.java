package io.beanmapper.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class CanonicalClassNameStore {

    private static CanonicalClassNameStore INSTANCE;

    private final Map<Class<?>, String> classNames;

    private CanonicalClassNameStore() {
        classNames = new ConcurrentHashMap<>();
    }

    public String getOrComputeClassName(Class<?> clazz) {
        String className = classNames.get(clazz);
        if (className == null) {
            className = CanonicalClassName.determineCanonicalClassName(clazz);
            classNames.put(clazz, className);
        }
        return className;
    }

    public static synchronized CanonicalClassNameStore getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CanonicalClassNameStore();
        }
        return INSTANCE;
    }
}
