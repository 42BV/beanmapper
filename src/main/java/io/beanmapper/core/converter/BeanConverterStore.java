package io.beanmapper.core.converter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class BeanConverterStore {

    private final Map<Class<?>, Map<Class<?>, BeanConverter>> beanConverterMap;

    public BeanConverterStore() {
        this.beanConverterMap = new ConcurrentHashMap<>();
    }

    public BeanConverterStore(BeanConverterStore beanConverterStore) {
        this(beanConverterStore.getBeanConverterMap());
    }

    public BeanConverterStore(Map<Class<?>, Map<Class<?>, BeanConverter>> beanConverterMap) {
        this.beanConverterMap = new ConcurrentHashMap<>();
        beanConverterMap.forEach((key, value) -> this.beanConverterMap.put(key, new HashMap<>(value)));
    }

    public synchronized BeanConverter get(Class<?> source, Class<?> target) {
        var cachedConverters = beanConverterMap.get(source);

        if (cachedConverters == null) {
            beanConverterMap.put(source, new HashMap<>());
            return null;
        }

        return cachedConverters.get(target);
    }

    public synchronized void add(Class<?> source, Class<?> target, BeanConverter beanConverter) {
        var cachedConverters = beanConverterMap.get(source);

        if (cachedConverters == null) {
            beanConverterMap.putIfAbsent(source, new HashMap<>());
            cachedConverters = beanConverterMap.get(source);
            cachedConverters.put(target, beanConverter);
        }

        cachedConverters.put(target, beanConverter);
    }

    private Map<Class<?>, Map<Class<?>, BeanConverter>> getBeanConverterMap() {
        return this.beanConverterMap;
    }
}
