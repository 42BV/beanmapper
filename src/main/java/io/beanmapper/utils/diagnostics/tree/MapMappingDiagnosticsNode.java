package io.beanmapper.utils.diagnostics.tree;

import java.util.Map;
import java.util.Objects;

import io.beanmapper.utils.CanonicalClassNameStore;

/**
 * Represents a diagnostic node for mapping between two map classes.
 *
 * @param <S>  the type of the source map
 * @param <T>  the type of the target map
 * @param <U> the type of the elements in the source map
 * @param <R> the type of the elements in the target map
 * @param <K>  the type of the keys in the map
 */
public final class MapMappingDiagnosticsNode<S extends Map<K, U>, T, U, R, K> extends CollectionMappingDiagnosticNode<S, T, U, R> {

    private final Class<K> keyClass;

    public MapMappingDiagnosticsNode(S source, Class<S> sourceClass, Class<T> targetClass, Class<R> targetElementClass) {
        super(source, sourceClass, targetClass, targetElementClass);
        this.keyClass = determineKeyClass(source);
    }

    /**
     * Determines the key class of the source map.
     *
     * @param source the source collection
     * @return the key class of the source map, or Void.class if the collection is empty
     */
    @SuppressWarnings("unchecked")
    private Class<K> determineKeyClass(S source) {
        if (source.isEmpty()) {
            return (Class<K>) Void.class;
        }
        K key = source.keySet().iterator().next();
        return (Class<K>) key.getClass();
    }

    /**
     * <p>Returns a string representation of the object.</p>
     *
     * <p>This method returns a formatted string representing a mapping between two classes. The string includes the class names of
     * the source and target classes, as well as the class names of the key and element types for the source and target collections.</p>
     *
     * <p>The format of the string is as follows:<br>
     * <b>"[Mapping] %s&lt;%s, %s&gt; -> %s&lt;%s, %s&gt;"</b></p>
     *
     * <p>where:<br>
     * - %s represents a placeholder for a string value<br>
     * - The first %s is replaced with the simple name of the source class<br>
     * - The second %s is replaced with the simple name of the key class for the source collection<br>
     * - The third %s is replaced with the simple name of the elements class for the source collection<br>
     * - The fourth %s is replaced with the simple name of the target class<br>
     * - The fifth %s is replaced with the simple name of the key class for the target collection<br>
     * - The sixth %s is replaced with the simple name of the elements class for the target collection</p>
     *
     * <p>For example, if called on a MapMappingDiagnosticsNode object with the following class types:<br>
     * - sourceClass = HashMap<br>
     * - keyClass = Integer<br>
     * - sourceElementsClass = String<br>
     * - targetClass = TreeMap<br>
     * - targetElementsClass = Double</p>
     *
     * <p>The method would return the following string:<br>
     * <b>"[Mapping   ] HashMap&lt;Integer, String&gt; -> TreeMap&lt;Integer, Double&gt;"</b></p>
     *
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        String sourceClassName = CanonicalClassNameStore.getInstance().getOrComputeClassName(sourceClass);
        String targetClassName = CanonicalClassNameStore.getInstance().getOrComputeClassName(targetClass);
        String sourceElementsClassName = CanonicalClassNameStore.getInstance().getOrComputeClassName(sourceElementsClass);
        String targetElementsClassName = CanonicalClassNameStore.getInstance().getOrComputeClassName(targetElementsClass);
        String keyClassName = CanonicalClassNameStore.getInstance().getOrComputeClassName(keyClass);
        return "[Mapping   ] %s<%s, %s> -> %s<%s, %s>".formatted(sourceClassName, keyClassName, sourceElementsClassName,
                targetClassName, keyClassName, targetElementsClassName);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        MapMappingDiagnosticsNode<?, ?, ?, ?, ?> that = (MapMappingDiagnosticsNode<?, ?, ?, ?, ?>) obj;
        return Objects.equals(keyClass, that.keyClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), keyClass);
    }
}
