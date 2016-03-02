package io.beanmapper;

import io.beanmapper.config.BeanMapperBuilder;
import io.beanmapper.config.Configuration;
import io.beanmapper.config.CoreConfiguration;
import io.beanmapper.core.constructor.BeanInitializer;
import io.beanmapper.core.rule.MappableFields;
import io.beanmapper.exceptions.BeanMappingException;
import io.beanmapper.strategy.MapStrategyType;

import java.util.Collection;

/**
 * Class that is responsible first for understanding the semantics of the source and target
 * objects. Once that has been determined, the applicable properties will be copied from
 * source to target.
 */
@SuppressWarnings("unchecked")
public class BeanMapper {

    Configuration configuration = new CoreConfiguration();

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
     * Copies the values from the source object to a newly constructed target instance
     * @param source source instance of the properties
     * @param targetClass class of the target, needs to be constructed as the target instance
     * @param <S> The instance from which the properties get copied
     * @param <T> the instance to which the properties get copied
     * @return the target instance containing all applicable properties
     * @throws BeanMappingException
     */
    public <S, T> T map(S source, Class<T> targetClass) {

        return (T) config()
                .setTargetClass(targetClass)
                .setConverterChoosable(false)
                .build()
            .map(source);
    }

    /**
     * Copies the values from the source object to a newly constructed target instance
     * @param source source instance of the properties
     * @param targetClass class of the target, needs to be constructed as the target instance
     * @param converterChoosable when {@code true} a plain conversion is checked before mapping
     * @param <S> The instance from which the properties get copied
     * @param <T> the instance to which the properties get copied
     * @return the target instance containing all applicable properties
     * @throws BeanMappingException
     */
    @SuppressWarnings("unchecked")
    public <S, T> T map(S source, Class<T> targetClass, boolean converterChoosable) {

        return (T) config()
                .setTargetClass(targetClass)
                .setConverterChoosable(converterChoosable)
                .build()
            .map(source);
    }
    
    /**
     * Copies the values from the source object to a newly constructed target instance
     * @param source source instance of the properties
     * @param targetClass class of the target, needs to be constructed as the target instance
     * @param beanInitializer initializes the beans
     * @param converterChoosable when {@code true} a plain conversion is checked before mapping
     * @param <S> The instance from which the properties get copied
     * @param <T> the instance to which the properties get copied
     * @return the target instance containing all applicable properties
     * @throws BeanMappingException
     */
    @SuppressWarnings("unchecked")
    public <S, T> T map(S source, Class<T> targetClass, BeanInitializer beanInitializer, boolean converterChoosable) {

        return (T) config()
                .setTargetClass(targetClass)
                .setBeanInitializer(beanInitializer)
                .setConverterChoosable(converterChoosable)
                .build()
            .map(source);
    }

    /**
     * Maps a list of source items to a list of target items with a specific class
     * @param sourceItems the items to be mapped
     * @param targetClass the class type of the items in the returned list
     * @param <S> the instance from which the properties get copied.
     * @param <T> the instance to which the properties get copied
     * @return the list of mapped items with class T
     * @throws BeanMappingException
     */
    @SuppressWarnings("unchecked")
    public <S, T> Collection<T> map(Collection<S> sourceItems, Class<T> targetClass) {

        return (Collection<T>) config()
                .setTargetClass(targetClass)
                .setCollectionClass(sourceItems.getClass())
                .build()
            .map(sourceItems);
    }
    
    /**
     * Maps a list of source items to a list of target items with a specific class
     * @param sourceItems the items to be mapped
     * @param targetClass the class type of the items in the returned list
     * @param collectionClass the collection class
     * @param <S> the instance from which the properties get copied.
     * @param <T> the instance to which the properties get copied
     * @return the list of mapped items with class T
     * @throws BeanMappingException
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <S, T> Collection<T> map(Collection<S> sourceItems, Class<T> targetClass, Class<? extends Collection> collectionClass) {

        return (Collection<T>) config()
                .setTargetClass(targetClass)
                .setCollectionClass(collectionClass)
                .build()
            .map(sourceItems);
    }

    /**
     * Copies the values from the source object to an existing target instance
     * @param source source instance of the properties
     * @param target target instance for the properties
     * @param <S> the instance from which the properties get copied.
     * @param <T> the instance to which the properties get copied
     * @return the original target instance containing all applicable properties
     * @throws BeanMappingException
     */
    public <S, T> T map(S source, T target) {

        return (T) config()
                .setTarget(target)
                .build()
            .map(source);
    }
    
    /**
     * Copies the values from the source object to an existing target instance
     * @param source source instance of the properties
     * @param target target instance for the properties
     * @param <S> the instance from which the properties get copied.
     * @param <T> the instance to which the properties get copied
     * @return the original target instance containing all applicable properties
     * @throws BeanMappingException
     */
    public <S, T> T map(S source, T target, MappableFields fieldsToMap) {

        return (T) config()
                .setTarget(target)
                .setMappableFields(fieldsToMap)
                .build()
            .map(source);
    }

    public final Configuration getConfiguration() {
        return configuration;
    }

    public BeanMapperBuilder clear() {
        return BeanMapperBuilder
                .config(configuration)
                .setIncludeFields(null)
                .setCollectionClass(null)
                .setTargetClass(null)
                .setTarget(null);
    }

    public BeanMapperBuilder config() {
        return BeanMapperBuilder.config(configuration);
    }

    public BeanMapperBuilder wrapConfig() {
        return BeanMapperBuilder.wrapConfig(configuration);
    }

}
