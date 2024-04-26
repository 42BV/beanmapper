package io.beanmapper.utils.diagnostics.tree;

import java.util.Map;

import io.beanmapper.core.converter.BeanConverter;
import io.beanmapper.utils.CanonicalClassNameStore;

/**
 * Represents a diagnostic node for a map conversion operation.
 *
 * @param <S>  the source map type
 * @param <T>  the target map type
 * @param <U> the source element type
 * @param <R> the target element type
 * @param <K>  the key type of the map
 * @param <C>  the converter type
 */
public final class MapConversionDiagnosticNode<S extends Map, T extends Map, U, R, K, C extends BeanConverter>
        extends CollectionConversionDiagnosticNode<S, T, U, R, C> {

    private final Class<K> keyClass;

    public MapConversionDiagnosticNode(S source, Class<S> sourceClass, Class<T> targetClass, Class<R> targetElementClass, Class<C> converterClass) {
        super(source, sourceClass, targetClass, targetElementClass, converterClass);
        this.keyClass = determineKeyClass(source);
    }

    @SuppressWarnings("unchecked")
    private Class<K> determineKeyClass(S source) {
        if (source.isEmpty()) {
            return (Class<K>) Void.class;
        }
        K key = (K) source.keySet().iterator().next();
        return (Class<K>) key.getClass();
    }

    @Override
    public String toString() {
        String sourceClassName = CanonicalClassNameStore.getInstance().getOrComputeClassName(sourceClass);
        String targetClassName = CanonicalClassNameStore.getInstance().getOrComputeClassName(targetClass);
        String keyClassName = CanonicalClassNameStore.getInstance().getOrComputeClassName(keyClass);
        String sourceElementClassName = CanonicalClassNameStore.getInstance().getOrComputeClassName(sourceElementClass);
        String targetElementClassName = CanonicalClassNameStore.getInstance().getOrComputeClassName(targetElementClass);
        return "[Conversion] %s<%s, %s> -> %s<%s, %s>".formatted(sourceClassName, keyClassName, sourceElementClassName, targetClassName, keyClassName,
                targetElementClassName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof MapConversionDiagnosticNode<?, ?, ?, ?, ?, ?> that))
            return false;
        if (!super.equals(o))
            return false;
        return keyClass.equals(that.keyClass);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + keyClass.hashCode();
    }

    public static <S extends Map, T extends Map, U, R, K, C extends BeanConverter> MapConversionDiagnosticNode<S, T, U, R, K, C> of(S source, Class<S> sourceClass, Class<T> targetClass, Class<R> targetElementClass, Class<C> converterClass) {
        return new MapConversionDiagnosticNode<>(source, sourceClass, targetClass, targetElementClass, converterClass);
    }
}
