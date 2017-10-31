package io.beanmapper;

import java.util.List;
import java.util.Map;
import java.util.Set;

import io.beanmapper.config.BeanMapperBuilder;
import io.beanmapper.config.Configuration;
import io.beanmapper.config.CoreConfiguration;
import io.beanmapper.strategy.MapStrategyType;

/**
 * Class that is responsible first for understanding the semantics of the source and target
 * objects. Once that has been determined, the applicable properties will be copied from
 * source to target.
 */
@SuppressWarnings("unchecked")
public class BeanMapper {

    private Configuration configuration = new CoreConfiguration();

    public BeanMapper(Configuration configuration) {
        this.configuration = configuration;
    }

    public Object map(Object source) {
        if (source == null) {
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
     * Maps the source map of elements to a new target map. Convenience operator
     * @param map the source map
     * @param mapValueClass the class of each value in the target map
     * @param <K> the class type of a key in both source and target map
     * @param <T> the class type of a value in the target map
     * @param <S> the class type of the source map
     * @return the target map with literal source set keys and mapped source set values
     */
    public <K, S, T> Map<K,T> map(Map<K,S> map, Class<T> mapValueClass) {
        return (Map<K,T>) mapCollection(map, mapValueClass);
    }

    private Object mapCollection(Object collection, Class<?> elementInCollection) {
        return wrap()
                .setCollectionClass(collection.getClass())
                .setTargetClass(elementInCollection)
                .build()
                .map(collection);
    }

    public final Configuration getConfiguration() {
        return configuration;
    }

    /**
     * @deprecated use wrap() instead
     */
    public BeanMapperBuilder config() {
        return wrap();
    }

    /**
     * @deprecated use wrap() instead
     */
    public BeanMapperBuilder wrapConfig() {
        return wrap();
    }

    public BeanMapperBuilder wrap() {
        return new BeanMapperBuilder(configuration);
    }

}
