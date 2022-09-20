package io.beanmapper;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import io.beanmapper.config.BeanMapperBuilder;
import io.beanmapper.config.Configuration;
import io.beanmapper.core.collections.Equalizer;
import io.beanmapper.exceptions.BeanConversionException;
import io.beanmapper.strategy.MapStrategyType;

/**
 * Class that is responsible first for understanding the semantics of the source and target
 * objects. Once that has been determined, the applicable properties will be copied from
 * source to target.
 */
@SuppressWarnings("unchecked")
public record BeanMapper(Configuration configuration) {

    public Object map(Object source) {
        if (source == null && !configuration.getUseNullValue()) {
            return null;
        }
        return MapStrategyType.getStrategy(this, configuration).map(source);
    }

    /**
     * Copies the values from the source object to an existing target instance
     * @param source source instance of the properties
     * @param target target instance for the properties
     * @param <S> the instance from which the properties get copied.
     * @param <T> the instance to which the properties get copied
     * @return the original target instance containing all applicable properties
     */
    public <S, T> T map(S source, T target) {
        return (T) wrap()
                .setTarget(target)
                .build()
                .map(source);
    }

    /**
     * Copies the values from the optional source object to a newly constructed target instance
     * @param source optional source instance of the properties
     * @param targetClass class of the target, needs to be constructed as the target instance
     * @param <S> the instance from which the properties get copied
     * @param <T> the instance to which the properties get copied
     * @return the optional target instance containing all applicable properties
     */
    public <S, T> Optional<T> map(Optional<S> source, Class<T> targetClass) {
        return source.map(value -> this.map(value, targetClass));
    }

    /**
     * Copies the values from the source object to a newly constructed target instance
     * @param source source instance of the properties
     * @param targetClass class of the target, needs to be constructed as the target instance
     * @param <S> The instance from which the properties get copied
     * @param <T> the instance to which the properties get copied
     * @return the target instance containing all applicable properties
     */
    public <S, T> T map(S source, Class<T> targetClass) {
        return (T) wrap()
                .setTargetClass(targetClass)
                .build()
                .map(source);
    }

    /**
     * Maps the source list of elements to a new target list. Convenience operator
     * @param list the source list
     * @param elementInListClass the class of each element in the target list
     * @param <S> the class type of the source list
     * @param <T> the class type of an element in the target list
     * @return the target list with mapped source list elements
     */
    public <S, T> List<T> map(List<S> list, Class<T> elementInListClass) {
        return (List<T>) mapCollection(list, elementInListClass);
    }

    /**
     * Maps the source set of elements to a new target set. Convenience operator
     * @param set the source set
     * @param elementInSetClass the class of each element in the target set
     * @param <S> the class type of the source set
     * @param <T> the class type of an element in the target set
     * @return the target set with mapped source set elements
     */
    public <S, T> Set<T> map(Set<S> set, Class<T> elementInSetClass) {
        return (Set<T>) mapCollection(set, elementInSetClass);
    }

    /**
     * Maps a Collection of elements into another, existing Collection of elements, updating the corresponding entities, and inserting elements without
     * counterpart.
     * @param source the source collection
     * @param target the target collection
     * @param <S> the class type of the elements in the  source collection, which must extend from Equalizer<ID>
     * @param <T> the class type of the elements in the target collection, which must extend from Equalizer<ID>
     * @return the combination of the source and target collections, where the elements of the source collection have been mapped to the target class.
     */
    public <S extends Equalizer, T extends Equalizer> Collection<T> map(Collection<S> source, Collection<T> target) {
        if (target.isEmpty()) {
            throw new RuntimeException("");
        }


        Set<T> set = new HashSet<>();
        for (S entity : source) {
            T t = null;
            for (T otherEntity : target) {
                if (entity.isEqual(otherEntity)) {
                    t = map(entity, otherEntity);
                    set.add(t);
                    break;
                }
            }
            if (t == null && !this.configuration.getOnlyPatchExistingDuringCollectionToCollection()) {
                if (target.isEmpty()) {
                    throw new BeanConversionException(entity.getClass(), target.getClass());
                }
                Class<T> clazz = (Class<T>) target.stream().findFirst().get().getClass();
                set.add(map(entity, clazz));
            }
        }
        return set;
    }

    public <S extends Equalizer, T extends Equalizer> Collection<T> map(Collection<S> source, Collection<T> target, Class<T> targetClass) {
        // It's probably a better idea to pass the targetClass as a parameter. So this method will replace the method above this one.
        return null;
    }

    /**
     *Maps a Map of elements into another, existing Map of elements, updating the corresponding entities, and inserting elements without counterpart.
     * @param source the source map
     * @param target the target map
     * @param targetClass the class-object representing the type of the elements in the target map.
     * @param <K> the class of the key/ID of each element.
     * @param <V1> the class type of the elements in the source map, which must extend from Equalizer<K>
     * @param <V2> the class type of the elements in the target map, which must extend from Equalizer<K>
     * @return the combination of the source and target maps, where the elements of the source map have been mapped to the target class.
     */
    public <K, V1 extends Equalizer, V2 extends Equalizer> Map<K, V2> map(Map<K, V1> source, Map<K, V2> target, Class<V2> targetClass) {
        Map<K, V2> result = new HashMap<>();
        for (var entry : source.entrySet()) {
            boolean isMapped = false;
            for (var targetEntry : target.entrySet()) {
                if (entry.getValue().isEqual(targetEntry.getValue())) {
                    result.put(targetEntry.getKey(), this.map(entry.getValue(), targetClass));
                    isMapped = true;
                    break;
                }
            }
            if (!(isMapped || this.configuration.getOnlyPatchExistingDuringCollectionToCollection())) {
                result.put(entry.getKey(), this.map(entry.getValue(), targetClass));
            }
        }
        return result;
    }

    /**
     * Maps the source map of elements to a new target map. Convenience operator
     * @param map the source map
     * @param mapValueClass the class of each value in the target map
     * @param <K> the class type of a key in both source and target map
     * @param <T> the class type of a value in the target map
     * @param <S> the class type of the source map
     * @return the target map with literal source set keys and mapped source set values
     */
    public <K, S, T> Map<K, T> map(Map<K, S> map, Class<T> mapValueClass) {
        return (Map<K, T>) mapCollection(map, mapValueClass);
    }

    private Object mapCollection(Object collection, Class<?> elementInCollection) {
        return wrap()
                .setCollectionClass(collection.getClass())
                .setTargetClass(elementInCollection)
                .build()
                .map(collection);
    }

    /**
     * @deprecated use wrap() instead
     * @return BeanMapperBuilder
     */
    @Deprecated(forRemoval = true)
    public BeanMapperBuilder config() {
        return wrap();
    }

    /**
     * @deprecated use wrap() instead
     * @return BeanMapperBuilder
     */
    @Deprecated(forRemoval = true)
    public BeanMapperBuilder wrapConfig() {
        return wrap();
    }

    public BeanMapperBuilder wrap() {
        return new BeanMapperBuilder(configuration);
    }

}
