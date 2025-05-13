package io.beanmapper;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

import io.beanmapper.config.BeanMapperBuilder;
import io.beanmapper.config.Configuration;
import io.beanmapper.config.DiagnosticsConfiguration;
import io.beanmapper.config.DiagnosticsConfigurationImpl;
import io.beanmapper.config.OverrideConfiguration;
import io.beanmapper.strategy.MapStrategyType;
import io.beanmapper.utils.diagnostics.DiagnosticsDetailLevel;
import io.beanmapper.utils.diagnostics.tree.CollectionMappingDiagnosticNode;
import io.beanmapper.utils.diagnostics.tree.DiagnosticsNode;
import io.beanmapper.utils.diagnostics.tree.MapMappingDiagnosticsNode;
import io.beanmapper.utils.diagnostics.tree.MappingDiagnosticsNode;

/**
 * Class that is responsible first for understanding the semantics of the source and target
 * objects. Once that has been determined, the applicable properties will be copied from
 * source to target.
 */
public record BeanMapper(Configuration configuration) {

    @SuppressWarnings("unchecked")
    private <S, T> void addDiagnosticsNode(S source) {
        if (configuration instanceof DiagnosticsConfiguration dc && dc.isInDiagnosticsMode()) {
            if (dc.getParentConfiguration().orElse(null) instanceof DiagnosticsConfigurationImpl dci) {
                dci.getBeanMapperDiagnostics().ifPresent(bd -> bd.getDiagnostics().clear());
            }
            Class<S> sourceClass = source != null ? (Class<S>) configuration.getBeanUnproxy().unproxy(source.getClass()) : (Class<S>) Void.class;
            DiagnosticsNode<S, T> node = getsDiagnosticsNode(source, sourceClass);
            dc.setBeanMapperDiagnostics(node);
            if (configuration instanceof OverrideConfiguration) {
                dc.getParentConfiguration()
                        .map(DiagnosticsConfiguration.class::cast)
                        .orElseThrow()
                        .getBeanMapperDiagnostics().ifPresent(bd -> bd.add(node));
            }
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private <S, T> DiagnosticsNode<S, T> getsDiagnosticsNode(S source, Class<S> sourceClass) {
        DiagnosticsNode<S, T> node = new MappingDiagnosticsNode<>(sourceClass, configuration.getTargetClass());
        if (source instanceof Collection<?>) {
            node = new CollectionMappingDiagnosticNode<>(source, sourceClass, configuration.getPreferredCollectionClass(), configuration.getTargetClass());
        }
        if (source instanceof Map<?, ?> map) {
            node = new MapMappingDiagnosticsNode<>(map, (Class<Map>) sourceClass, configuration.getPreferredCollectionClass(),
                    configuration.getTargetClass());
        }
        return node;
    }

    public <S, T> T map(S source) {
        addDiagnosticsNode(source);
        if (source == null && !configuration.getUseNullValue()) {
            // noinspection unchecked
            return (T) this.configuration().getDefaultValueForClass(this.configuration().getTargetClass());
        }
        T result = MapStrategyType.getStrategy(this, configuration).map(source);
        if (this.configuration.getParentConfiguration().orElseThrow() instanceof DiagnosticsConfigurationImpl dc) {
            dc.getDiagnosticsLogger().ifPresent(dl -> dl.log(((DiagnosticsConfiguration) configuration).getBeanMapperDiagnostics().orElse(null)));
        }
        return result;
    }

    /**
     * Copies the values from the source object to an existing target instance
     *
     * @param source source instance of the properties
     * @param target target instance for the properties
     * @param <S>    the instance from which the properties get copied.
     * @param <T>    the instance to which the properties get copied
     * @return the original target instance containing all applicable properties
     */
    public <S, T> T map(S source, T target) {
        return wrap()
                .setTarget(target)
                .build()
                .map(source);
    }

    /**
     * Copies the values from the optional source object to a newly constructed target instance
     *
     * @param source      optional source instance of the properties
     * @param targetClass class of the target, needs to be constructed as the target instance
     * @param <S>         the instance from which the properties get copied
     * @param <T>         the instance to which the properties get copied
     * @return the optional target instance containing all applicable properties
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public <S, T> Optional<T> map(Optional<S> source, Class<T> targetClass) {
        if (source.orElse(null) instanceof Optional<?> optional && !targetClass.isInstance(Optional.class)) {
            return this.map(optional, targetClass);
        }
        return source.map(value -> this.map(value, targetClass));
    }

    /**
     * Maps the source to the given target.
     *
     * <p>If the target is a Collection, Map, or Optional, the object wrapped in the source, will be mapped to the type
     * corresponding to the relevant type argument.</p>
     *
     * @param source Source instance of the properties.
     * @param target Implementation of ParameterizedType, which provides the information necessary to map elements to
     *               the correct type.
     * @param <S>    Type of the source.
     * @param <P>    Type of the specific implementation of ParameterizedType used as the target.
     * @return The result of the mapping.
     */
    public <S, P extends ParameterizedType> Object map(S source, P target) {
        return switch (source) {
            case Collection<?> collection -> this.map(collection, (Class<?>) target.getActualTypeArguments()[0]);
            case Map<?, ?> map -> this.map(map, (Class<?>) target.getActualTypeArguments()[1]);
            case Optional<?> optional -> this.map(optional, (Class<?>) target.getActualTypeArguments()[0]);
            default -> this.map(source, (Class<?>) target.getRawType());
        };
    }

    /**
     * Copies the values from the source object to a newly constructed target instance
     *
     * @param source      source instance of the properties
     * @param targetClass class of the target, needs to be constructed as the target instance
     * @param <S>         The instance from which the properties get copied
     * @param <T>         the instance to which the properties get copied
     * @return the target instance containing all applicable properties
     */
    public <S, T> T map(S source, Class<T> targetClass) {
        return wrap()
                .setTargetClass(targetClass)
                .build()
                .map(source);
    }

    /**
     * Maps the source array to an array with the type of the target class.
     *
     * @param sourceArray The source array.
     * @param targetClass The class to which the elements from the source array will be converted to.
     * @param <S>         The type of the elements in the source array.
     * @param <T>         The type of the elements in the target array.
     * @return A newly constructed array of the type of the target class.
     */
    @SuppressWarnings("unchecked")
    public <S, T> T[] map(S[] sourceArray, Class<T> targetClass) {
        return Arrays.stream(sourceArray)
                .map(element -> this.wrap().setConverterChoosable(true).build().map(element, targetClass))
                .toArray(element -> (T[]) Array.newInstance(targetClass, sourceArray.length));
    }

    /**
     * Maps the source collection to a new target collection.
     *
     * <p>The type of the target collection is determined automatically from the actual type of the source collection.
     * If the source </p>
     *
     * @param collection               - The source collection
     * @param elementInCollectionClass - The class of each element in the target list.
     * @param <S>                      The type up the elements in the source collection.
     * @param <T>                      The type of the target, to which the source elements will be mapped.
     * @return The target collection with mapped source collection elements.
     */
    public <S, T> Collection<T> map(Collection<S> collection, Class<T> elementInCollectionClass) {
        return mapCollection(collection, elementInCollectionClass);
    }

    /**
     * Maps the source list of elements to a new target list. Convenience operator
     *
     * @param list               the source list
     * @param elementInListClass the class of each element in the target list
     * @param <S>                the class type of the source list
     * @param <T>                the class type of an element in the target list
     * @return the target list with mapped source list elements
     */
    public <S, T> List<T> map(List<S> list, Class<T> elementInListClass) {
        return mapCollection(list, elementInListClass);
    }

    /**
     * Maps the source set of elements to a new target set. Convenience operator
     *
     * @param set               the source set
     * @param elementInSetClass the class of each element in the target set
     * @param <S>               the class type of the source set
     * @param <T>               the class type of an element in the target set
     * @return the target set with mapped source set elements
     */
    public <S, T> Set<T> map(Set<S> set, Class<T> elementInSetClass) {
        return mapCollection(set, elementInSetClass);
    }

    /**
     * Maps the source queue to a new target queue. Convenience operator.
     *
     * @param queue               The source queue.
     * @param elementInQueueClass The class of each element in the target queue.
     * @param <S>                 The class type of the source queue.
     * @param <T>                 The class type of the elements in the target queue.
     * @return The target queue with mapped source queue elements.
     */
    public <S, T> Queue<T> map(Queue<S> queue, Class<T> elementInQueueClass) {
        return mapCollection(queue, elementInQueueClass);
    }

    /**
     * Maps the source map of elements to a new target map. Convenience operator
     *
     * @param map           the source map
     * @param mapValueClass the class of each value in the target map
     * @param <K>           the class type of a key in both source and target map
     * @param <T>           the class type of a value in the target map
     * @param <S>           the class type of the source map
     * @return the target map with literal source set keys and mapped source set values
     */
    public <K, S, T> Map<K, T> map(Map<K, S> map, Class<T> mapValueClass) {
        return mapCollection(map, mapValueClass);
    }

    private <S, T, E> T mapCollection(S collection, Class<E> elementInCollection) {
        return wrap()
                .setCollectionClass(collection.getClass())
                .setTargetClass(elementInCollection)
                .build()
                .map(collection);
    }

    public BeanMapperBuilder wrap() {
        return wrap(DiagnosticsDetailLevel.DISABLED);
    }

    public BeanMapperBuilder wrap(DiagnosticsDetailLevel detailLevel) {
        return new BeanMapperBuilder(configuration, detailLevel);
    }

    public static BeanMapperBuilder builder() {
        return new BeanMapperBuilder();
    }
}
