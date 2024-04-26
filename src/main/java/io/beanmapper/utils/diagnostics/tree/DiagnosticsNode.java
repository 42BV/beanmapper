package io.beanmapper.utils.diagnostics.tree;

import java.util.ArrayList;
import java.util.List;

import io.beanmapper.utils.Pair;

/**
 * The DiagnosticsNode class is an abstract sealed class that serves as a base class for diagnostic nodes in the bean mapping process.
 * It encapsulates information about the source and target classes, as well as a list of subdiagnostics and the depth of the node in the diagnostic tree.
 * The class provides various methods to manipulate and access the diagnostics and their information.
 *
 * @param <S> the type of the source class
 * @param <T> the type of the target class
 */
public abstract sealed class DiagnosticsNode<S, T> permits MappingDiagnosticsNode, ConversionDiagnosticsNode {

    protected final Class<S> sourceClass;
    protected Class<T> targetClass;
    protected final List<DiagnosticsNode<?, ?>> diagnostics;
    protected int depth;

    protected DiagnosticsNode(Class<S> sourceClass, Class<T> targetClass) {
        this.sourceClass = sourceClass != null ? sourceClass : (Class<S>) Void.class;
        this.targetClass = targetClass != null ? targetClass : (Class<T>) Void.class;
        this.depth = 0;
        this.diagnostics = new ArrayList<>();
    }

    /**
     * Adds a diagnostic node to the list of diagnostics.
     * The depth of the diagnostic node is set to the depth of the current node plus one.
     *
     * @param diagnostic the diagnostic node to be added
     */
    public void add(DiagnosticsNode<?, ?> diagnostic) {
        diagnostic.depth = depth + 1;
        diagnostics.add(diagnostic);
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public List<DiagnosticsNode<?, ?>> getDiagnostics() {
        return diagnostics;
    }

    public Pair<Class<S>, Class<T>> getTypes() {
        return Pair.of(sourceClass, targetClass);
    }

    @SuppressWarnings("rawtypes")
    public List<MappingDiagnosticsNode> getMappingDiagnostics() {
        return diagnostics
                .stream()
                .filter(MappingDiagnosticsNode.class::isInstance)
                .map(c -> (MappingDiagnosticsNode) c)
                .toList();
    }

    @SuppressWarnings("rawtypes")
    public List<ConversionDiagnosticsNode> getConversionDiagnostics() {
        return diagnostics
                .stream()
                .filter(ConversionDiagnosticsNode.class::isInstance)
                .map(c -> (ConversionDiagnosticsNode) c)
                .toList();
    }

    public Class<S> getSourceClass() {
        return sourceClass;
    }

    public Class<T> getTargetClass() {
        return targetClass;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof DiagnosticsNode<?, ?> other) {
            return sourceClass.equals(other.sourceClass) && targetClass.equals(other.targetClass);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return sourceClass.hashCode() + targetClass.hashCode();
    }
}
