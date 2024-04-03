package io.beanmapper.core.unproxy;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton responsible for storing the results of unproxy-results. Allows for fast and thread-safe retrieval of results.
 */
public final class UnproxyResultStore {

    private static volatile UnproxyResultStore INSTANCE;

    private final Map<Class<?>, Class<?>> unproxyResultClassStore;

    private UnproxyResultStore() {
        unproxyResultClassStore = Collections.synchronizedMap(new HashMap<>());
    }

    /**
     * Gets or computes the result of an unproxy-operation.
     *
     * @param source The class for which the unproxy-result needs to be retrieved or computed.
     * @param unproxy The BeanUnproxy that will be used to compute an unproxy-result if none exist for the given source-class.
     * @return The unproxied class.
     */
    public Class<?> getOrComputeUnproxyResult(Class<?> source, BeanUnproxy unproxy) {
        return unproxyResultClassStore.computeIfAbsent(source, unproxy::unproxy);
    }

    public static UnproxyResultStore getInstance() {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        synchronized (UnproxyResultStore.class) {
            if (INSTANCE == null) {
                INSTANCE = new UnproxyResultStore();
            }
        }
        return INSTANCE;
    }

}