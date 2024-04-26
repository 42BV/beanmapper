package io.beanmapper.utils.diagnostics.tree;

import io.beanmapper.utils.CanonicalClassNameStore;

public sealed class MappingDiagnosticsNode<S, T> extends DiagnosticsNode<S, T> permits CollectionMappingDiagnosticNode {

    public MappingDiagnosticsNode(Class<S> sourceClass, Class<T> targetClass) {
        super(sourceClass, targetClass);
    }

    @Override
    public String toString() {
        String sourceClassName = CanonicalClassNameStore.getInstance().getOrComputeClassName(sourceClass);
        String targetClassName = CanonicalClassNameStore.getInstance().getOrComputeClassName(targetClass);
        return "[Mapping   ] %s -> %s".formatted(sourceClassName, targetClassName);
    }
}
