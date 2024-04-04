package io.beanmapper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ExecutionPlanStore {

    private static ExecutionPlanStore INSTANCE;

    private final Map<Class<?>, Map<Class<?>, ExecutionPlan<?, ?>>> executionPlanCache;

    private ExecutionPlanStore() {
        executionPlanCache = new ConcurrentHashMap<>();
    }

    public <S, T> void add(Class<S> source, Class<T> target, ExecutionPlan<S, T> executionPlan) {
        executionPlanCache.computeIfAbsent(source, (s) -> new HashMap<>()).put(target, executionPlan);
    }
}
