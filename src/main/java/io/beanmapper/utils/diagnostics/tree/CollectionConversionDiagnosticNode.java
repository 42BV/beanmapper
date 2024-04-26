package io.beanmapper.utils.diagnostics.tree;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import io.beanmapper.core.converter.BeanConverter;
import io.beanmapper.utils.CanonicalClassNameStore;
import io.beanmapper.utils.Classes;

public sealed class CollectionConversionDiagnosticNode<S, T, U, R, C extends BeanConverter> extends ConversionDiagnosticsNode<S, T, C>
        permits MapConversionDiagnosticNode {

    protected final Class<U> sourceElementClass;
    protected final Class<R> targetElementClass;

    public CollectionConversionDiagnosticNode(S source, Class<S> sourceClass, Class<T> targetClass, Class<R> targetElementClass, Class<C> converterClass) {
        super(sourceClass, targetClass, converterClass);
        this.sourceElementClass = getSourceElementsClass(source);
        this.targetElementClass = targetElementClass;
    }

    protected Class<U> getSourceElementsClass(S source) {
        if (source instanceof Collection<?> collection)
            return Classes.getSourceElementClass(collection);
        if (source instanceof Map<?, ?> map) {
            return Classes.getSourceElementClass(map);
        }
        throw new IllegalArgumentException();
    }

    @Override
    public String toString() {
        String sourceClassName = CanonicalClassNameStore.getInstance().getOrComputeClassName(sourceClass);
        String sourceElementClassName = CanonicalClassNameStore.getInstance().getOrComputeClassName(sourceElementClass);
        String targetClassName = CanonicalClassNameStore.getInstance().getOrComputeClassName(targetClass);
        String targetElementClassName = CanonicalClassNameStore.getInstance().getOrComputeClassName(targetElementClass);
        String converterClassName = CanonicalClassNameStore.getInstance().getOrComputeClassName(converterClass);
        return "[Conversion] %s<%s> -> %s<%s> (%s)".formatted(sourceClassName, sourceElementClassName,
                targetClassName, targetElementClassName, converterClassName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        CollectionConversionDiagnosticNode<?, ?, ?, ?, ?> that = (CollectionConversionDiagnosticNode<?, ?, ?, ?, ?>) o;
        return Objects.equals(sourceElementClass, that.sourceElementClass) && Objects.equals(targetElementClass, that.targetElementClass);
    }
}
