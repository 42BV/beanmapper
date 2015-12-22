package io.beanmapper;

import io.beanmapper.annotations.BeanConstruct;
import io.beanmapper.annotations.BeanDefault;
import io.beanmapper.annotations.BeanProperty;
import io.beanmapper.config.BeanMapperBuilder;
import io.beanmapper.config.Configuration;
import io.beanmapper.config.CoreConfiguration;
import io.beanmapper.core.BeanField;
import io.beanmapper.core.BeanFieldMatch;
import io.beanmapper.core.BeanMatch;
import io.beanmapper.core.constructor.BeanInitializer;
import io.beanmapper.core.converter.BeanConverter;
import io.beanmapper.core.rule.MappableFields;
import io.beanmapper.exceptions.BeanConversionException;
import io.beanmapper.exceptions.BeanFieldNoMatchException;
import io.beanmapper.exceptions.BeanMappingException;
import io.beanmapper.exceptions.BeanNoTargetException;
import io.beanmapper.utils.ConstructorArguments;

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

        // If a collection target class has been passed, we are dealing with a collection
        if (configuration.getCollectionClass() != null) {

            Collection targetItems = (Collection)configuration.getBeanInitializer().instantiate(configuration.getCollectionClass(), null);
            for (Object item : (Collection)source) {
                targetItems.add(map(item, configuration.getTargetClass()));
            }
            return targetItems;
        }

        final Object target;
        final BeanMatch beanMatch;

        if (configuration.getTargetClass() != null) {

            Class targetClass = configuration.getTargetClass();

            if (configuration.isConverterChoosable()) {
                Class<?> valueClass = configuration.getBeanUnproxy().unproxy(source.getClass());
                BeanConverter converter = getConverterOptional(valueClass, targetClass);
                if (converter != null) {
                    return converter.convert(source, targetClass, null);
                }
            }

            beanMatch = getBeanMatch(source.getClass(), targetClass);
            target = configuration.getBeanInitializer().instantiate(targetClass, getConstructorArguments(source, beanMatch));

        } else if (configuration.getTarget() != null) {

            target = configuration.getTarget();
            beanMatch = getBeanMatch(source.getClass(), target.getClass());
        } else {
            throw new BeanNoTargetException();
        }

        return processFields(
                source,
                target,
                configuration.getMappableFields() != null ?
                        configuration.getMappableFields().compressBeanMatch(beanMatch) :
                        beanMatch);
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

        return (T)config()
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

        return (T)config()
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

        return (T)config()
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

        return (Collection<T>)config()
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

        return (Collection<T>)config()
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

        return (T)config()
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

        return (T)config()
                .setTarget(target)
                .setMappableFields(fieldsToMap)
                .build()
            .map(source);
    }

    private <S> ConstructorArguments getConstructorArguments(S source, BeanMatch beanMatch) {
        BeanConstruct beanConstruct = beanMatch.getTargetClass().getAnnotation(BeanConstruct.class);

        // If the target does not have a BeanConstruct annotation, check the source
        if(beanConstruct == null){
            beanConstruct = beanMatch.getSourceClass().getAnnotation(BeanConstruct.class);
        }

        // If no BeanConstruct exists, assume a no-arg constructor must be used
        return beanConstruct == null ? null : new ConstructorArguments(source, beanMatch, beanConstruct.value());
    }

    private <T, S> BeanMatch getBeanMatch(Class<S> sourceClazz, Class<T> targetClazz) {
        Class<?> sourceClass = configuration.getBeanUnproxy().unproxy(sourceClazz);
        Class<?> targetClass = configuration.getBeanUnproxy().unproxy(targetClazz);
        return configuration.getBeanMatchStore().getBeanMatch(sourceClass, targetClass);
    }

    /**
     * Try to match the source fields on the target fields from the given classes.
     * First get all fields on the right level with method getAllFields.
     * Second match the fields and handle encapsulated classes.
     * Finally copy the data from the source to the target.
     *
     * @param source The source from which the values get copied.
     * @param target The target to which the values get copied.
     * @param <S>    The source type
     * @param <T>    The target type
     * @return A filled target object.
     * @throws BeanMappingException
     */
    private <S, T> T processFields(S source, T target, BeanMatch beanMatch) {
        for (String fieldName : beanMatch.getTargetNode().keySet()) {
            BeanField sourceField = beanMatch.getSourceNode().get(fieldName);
            if(sourceField == null) {
                // No source field found -> check for alias
                sourceField = beanMatch.getAliases().get(fieldName);
            }
            BeanField targetField = beanMatch.getTargetNode().get(fieldName);
            if(targetField == null) {
                // No target field found -> check for alias
                targetField = beanMatch.getAliases().get(fieldName);
            }
            processField(new BeanFieldMatch(source, target, sourceField, targetField, fieldName, beanMatch));
        }
        return target;
    }

    /**
     * Process a single combination of a source and a target field.
     * @param beanFieldMatch contains the fields belonging to the source/target field match
     * @throws BeanMappingException
     */
    private void processField(BeanFieldMatch beanFieldMatch) {
        if (!beanFieldMatch.hasMatchingSource()) {
            dealWithNonMatchingNode(beanFieldMatch);
            return;
        }
        if (!isConverterFor(beanFieldMatch.getSourceClass(), beanFieldMatch.getTargetClass()) &&
                (!beanFieldMatch.hasSimilarClasses() || (beanFieldMatch.hasSimilarClasses() && beanFieldMatch.getTargetObject() != null)) &&
                !(beanFieldMatch.getSourceClass().isEnum() || beanFieldMatch.getTargetClass().isEnum()) &&
                isMappableClass(beanFieldMatch.getTargetClass()) &&
                                beanFieldMatch.getSourceObject() != null) {

            dealWithMappableNestedClass(beanFieldMatch);
            return;
        }
        if (beanFieldMatch.isMappable()) {
            copySourceToTarget(beanFieldMatch);
        }
    }

    /**
     * This method is run when there is no matching source field for a target field. The result
     * could be that a default is set, or an exception is thrown when a BeanProperty has been set.
     * @param beanFieldMatch contains the fields belonging to the source/target field match
     */
    private void dealWithNonMatchingNode(BeanFieldMatch beanFieldMatch) {
        if (beanFieldMatch.targetHasAnnotation(BeanDefault.class)) {
            beanFieldMatch.setTarget(beanFieldMatch.getTargetDefaultValue());
        } else if (beanFieldMatch.targetHasAnnotation(BeanProperty.class)) {
            throw new BeanFieldNoMatchException("No source field found while attempting to map to " + beanFieldMatch.getTargetFieldName());
        }
    }

    /**
     * The copy action puts the source's value to the target. When @BeanDefault has been set and the
     * value to copy is empty, it will use the default.
     * @param beanFieldMatch contains the fields belonging to the source/target field match
     * @throws BeanMappingException
     */
    private void copySourceToTarget(BeanFieldMatch beanFieldMatch) {
        Object copyableSource = beanFieldMatch.getSourceObject();
        if (copyableSource == null) {
            if (beanFieldMatch.targetHasAnnotation(BeanDefault.class)) {
                copyableSource = beanFieldMatch.getTargetDefaultValue();
            } else if (beanFieldMatch.sourceHasAnnotation(BeanDefault.class)) {
                copyableSource = beanFieldMatch.getSourceDefaultValue();
            }
        }

        Object convertedValue = convert(copyableSource, beanFieldMatch.getTargetClass(), beanFieldMatch);
        beanFieldMatch.writeObject(convertedValue);
    }

    /**
     * If the field is a class which can itself be mapped to another class, it must be treated
     * as such. The matching process is called recursively to deal with this pair.
     * @param beanFieldMatch contains the fields belonging to the source/target field match
     * @throws BeanMappingException
     */
    private void dealWithMappableNestedClass(BeanFieldMatch beanFieldMatch) {
        Object encapsulatedSource = beanFieldMatch.getSourceObject();
        Object target;
        if (encapsulatedSource != null) {
            if(beanFieldMatch.getTargetObject() == null){
                target = map(encapsulatedSource, beanFieldMatch.getTargetClass());
            }else {
                if(beanFieldMatch.getBeanMatch().getMappableFields() == null) {
                    target = map(encapsulatedSource, beanFieldMatch.getTargetObject());
                } else {
                    target = map(encapsulatedSource, beanFieldMatch.getTargetObject(), beanFieldMatch.getBeanMatch().getMappableFields().splitForField(beanFieldMatch.getSourceFieldName()));
                }
            }
            beanFieldMatch.writeObject(target);
        }
    }

    /**
     * Verifies whether the class is part of the beans which may be mapped by the BeanMapper. This logic is
     * used when nested classes are encountered which need to be treated in a similar way as the main source/
     * target classes.
     * @param clazz the class to be verified against the allowed packages
     * @return true if the class may be mapped, false if it may not
     */
    public boolean isMappableClass(Class<?> clazz) {
        return clazz.getPackage() != null && isMappable(clazz.getPackage().getName());
    }
    
    /**
     * Verifies whether the package is part of the beans which may be mapped by the bean mapper. This logic is
     * used when nested classes are encountered which need to be treated in a similar way as the main source/
     * target classes.
     * @param packageName the package
     * @return true if the class may be mapped, false if it may not
     */
    public boolean isMappable(String packageName) {
        for (String packagePrefix : configuration.getPackagePrefixes()) {
            if (packageName.startsWith(packagePrefix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Converts a value into the target class.
     * @param value the value to convert
     * @param targetClass the target class
     * @return the converted value
     */
    @SuppressWarnings("unchecked")
    public Object convert(Object value, Class<?> targetClass, BeanFieldMatch beanFieldMatch) {
        if (value == null) {
            return null;
        }

        Class<?> valueClass = configuration.getBeanUnproxy().unproxy(value.getClass());

        BeanConverter converter = getConverterOptional(valueClass, targetClass);
        if (converter != null) {
            return converter.convert(value, targetClass, beanFieldMatch);
        }
        
        if (targetClass.isAssignableFrom(valueClass)) {
            return value;
        }

        throw new BeanConversionException(beanFieldMatch.getSourceClass(), targetClass);
    }

    private BeanConverter getConverterOptional(Class<?> sourceClass, Class<?> targetClass) {

        // Retrieve the first supported converter
        for (BeanConverter beanConverter : configuration.getBeanConverters()) {
            if (beanConverter != null && beanConverter.match(sourceClass, targetClass)) {
                return beanConverter;
            }
        }

        return null;
    }

    private boolean isConverterFor(Class<?> sourceClass, Class<?> targetClass) {
        return getConverterOptional(sourceClass, targetClass) != null;
    }
    
    public final Configuration getConfiguration() {
        return configuration;
    }

    public BeanMapperBuilder config() {
        return new BeanMapperBuilder(configuration);
    }

}
