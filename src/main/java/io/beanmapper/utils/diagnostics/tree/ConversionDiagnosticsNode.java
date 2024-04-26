package io.beanmapper.utils.diagnostics.tree;

import io.beanmapper.core.converter.BeanConverter;
import io.beanmapper.utils.CanonicalClassNameStore;

/**
 * The ConversionDiagnosticsNode class represents a diagnostic node in the bean mapping process
 * that encapsulates information about the conversion of a source class to a target class using a specific converter.
 *
 * @param <S> the type of the source class
 * @param <T> the type of the target class
 * @param <C> the type of the converter class
 */
public sealed class ConversionDiagnosticsNode<S, T, C extends BeanConverter> extends DiagnosticsNode<S, T> permits CollectionConversionDiagnosticNode {

    protected final Class<C> converterClass;

    public ConversionDiagnosticsNode(Class<S> sourceClass, Class<T> targetClass, Class<C> converterClass) {
        super(sourceClass, targetClass);
        this.converterClass = converterClass;
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ converterClass.hashCode();
    }

    @Override
    public String toString() {
        String sourceClassName = CanonicalClassNameStore.getInstance().getOrComputeClassName(sourceClass);
        String targetClassName = CanonicalClassNameStore.getInstance().getOrComputeClassName(targetClass);
        String converterClassName = CanonicalClassNameStore.getInstance().getOrComputeClassName(converterClass);
        return "[Conversion] %s -> %s (%s)".formatted(sourceClassName, targetClassName, converterClassName);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        ConversionDiagnosticsNode<?, ?, ?> that = (ConversionDiagnosticsNode<?, ?, ?>) o;
        return converterClass.equals(that.converterClass);
    }
}
