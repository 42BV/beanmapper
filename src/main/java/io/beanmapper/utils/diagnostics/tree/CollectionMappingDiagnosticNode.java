package io.beanmapper.utils.diagnostics.tree;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

import io.beanmapper.utils.CanonicalClassNameStore;
import io.beanmapper.utils.Classes;

public sealed class CollectionMappingDiagnosticNode<S, T, U, R> extends MappingDiagnosticsNode<S, T> permits MapMappingDiagnosticsNode {

    protected final Class<U> sourceElementsClass;
    protected final Class<R> targetElementsClass;

    public CollectionMappingDiagnosticNode(S source, Class<S> sourceClass, Class<T> targetClass, Class<R> targetElementsClass) {
        super(sourceClass, targetClass);
        if (targetClass == null) {
            super.targetClass = determineTargetClass(sourceClass);
        }
        this.sourceElementsClass = getSourceElementsClass(source);
        this.targetElementsClass = targetElementsClass;
    }

    private Class<T> determineTargetClass(Class<S> sourceClass) {
        if (List.class.isAssignableFrom(sourceClass)) {
            return (Class<T>) ArrayList.class;
        } else if (Set.class.isAssignableFrom(sourceClass)) {
            return (Class<T>) HashSet.class;
        } else if (Queue.class.isAssignableFrom(sourceClass)) {
            return (Class<T>) ArrayDeque.class;
        } else if (Map.class.isAssignableFrom(sourceClass)) {
            return (Class<T>) HashMap.class;
        }
        throw new IllegalArgumentException();
    }

    protected Class<U> getSourceElementsClass(S source) {
        if (source instanceof Collection<?> collection)
            return Classes.getSourceElementClass(collection);
        if (source instanceof Map<?,?> map) {
            return Classes.getSourceElementClass(map);
        }
        throw new IllegalArgumentException();
    }

    @Override
    public String toString() {
        String sourceClassName = CanonicalClassNameStore.getInstance().getOrComputeClassName(sourceClass);
        String targetClassName = CanonicalClassNameStore.getInstance().getOrComputeClassName(targetClass);
        String sourceElementClassName = CanonicalClassNameStore.getInstance().getOrComputeClassName(sourceElementsClass);
        String targetElementClassName = CanonicalClassNameStore.getInstance().getOrComputeClassName(targetElementsClass);
        return "[Mapping   ] %s<%s> -> %s<%s>".formatted(sourceClassName, sourceElementClassName, targetClassName, targetElementClassName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        CollectionMappingDiagnosticNode<?, ?, ?, ?> that = (CollectionMappingDiagnosticNode<?, ?, ?, ?>) o;
        return Objects.equals(sourceElementsClass, that.sourceElementsClass) && Objects.equals(targetElementsClass, that.targetElementsClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), sourceElementsClass, targetElementsClass);
    }
}
