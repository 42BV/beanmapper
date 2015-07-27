package io.beanmapper;

import io.beanmapper.annotations.BeanDefault;
import io.beanmapper.annotations.BeanProperty;
import io.beanmapper.core.BeanField;
import io.beanmapper.core.BeanFieldMatch;
import io.beanmapper.core.BeanMatch;
import io.beanmapper.core.BeanMatchStore;
import io.beanmapper.core.constructor.BeanInitializer;
import io.beanmapper.core.constructor.NoArgConstructorBeanInitializer;
import io.beanmapper.core.converter.BeanConverter;
import io.beanmapper.core.converter.impl.*;
import io.beanmapper.core.unproxy.BeanUnproxy;
import io.beanmapper.core.unproxy.DefaultBeanUnproxy;
import io.beanmapper.core.unproxy.SkippingBeanUnproxy;
import io.beanmapper.exceptions.BeanConversionException;
import io.beanmapper.exceptions.BeanFieldNoMatchException;
import io.beanmapper.exceptions.BeanMappingException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Class that is responsible first for understanding the semantics of the source and target
 * objects. Once that has been determined, the applicable properties will be copied from
 * source to target.
 */
public class BeanMapper {

    /**
     * Initializes the beans.
     */
    private BeanInitializer beanInitializer = new NoArgConstructorBeanInitializer();

    /**
     * Removes any potential proxies of beans.
     */
    private SkippingBeanUnproxy beanUnproxy = new SkippingBeanUnproxy(new DefaultBeanUnproxy());
    
    /**
     * Contains a store of matches for source and target class pairs. A pair is created only
     * once and reused every time thereafter.
     */
    private BeanMatchStore beanMatchStore = new BeanMatchStore();

    /**
     * The list of packages (and subpackages) containing classes which are eligible for mapping.
     */
    private List<Package> packagePrefixesForMappableClasses = new ArrayList<Package>();

    /**
     * The list of converters that should be checked for conversions.
     */
    private List<BeanConverter> beanConverters = new ArrayList<BeanConverter>();

    /**
     * Determines if the default converters must be added during the next conversion.
     */
    private boolean shouldAddDefaultConverters;

    /**
     * Construct a new bean mapper, with default converters.
     */
    public BeanMapper() {
        this(true);
    }
    
    /**
     * Construct a new bean mapper.
     * @param includeDefaultConverters whether default converters should be registered
     */
    public BeanMapper(boolean includeDefaultConverters) {
        shouldAddDefaultConverters = includeDefaultConverters;
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
        return map(source, targetClass, beanInitializer);
    }

    /**
     * Copies the values from the source object to a newly constructed target instance
     * @param source source instance of the properties
     * @param targetClass class of the target, needs to be constructed as the target instance
     * @param beanInitializer initializes the beans
     * @param <S> The instance from which the properties get copied
     * @param <T> the instance to which the properties get copied
     * @return the target instance containing all applicable properties
     * @throws BeanMappingException
     */
    public <S, T> T map(S source, Class<T> targetClass, BeanInitializer beanInitializer) {
        T target = beanInitializer.instantiate(targetClass);
        return map(source, target);
    }

    /**
     * Maps a list of source items to a list of target items with a specific class
     * @param sourceItems the items to be mapped
     * @param targetClass the class type of the items in the returned list
     * @param <S> source
     * @param <T> target
     * @return the list of mapped items with class T
     * @throws BeanMappingException
     */
    @SuppressWarnings("unchecked")
    public <S, T> Collection<T> map(Collection<S> sourceItems, Class<T> targetClass) {
        Collection<T> targetItems = (Collection<T>) beanInitializer.instantiate(sourceItems.getClass());
        for (S source : sourceItems) {
            targetItems.add(map(source, targetClass));
        }
        return targetItems;
    }

    /**
     * Copies the values from the source object to an existing target instance
     * @param source source instance of the properties
     * @param target target instance for the properties
     * @param <S> The instance from which the properties get copied.
     * @param <T> the instance to which the properties get copied
     * @return the original target instance containing all applicable properties
     * @throws BeanMappingException
     */
    public <S, T> T map(S source, T target) {
        return matchSourceToTarget(source, target);
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
    private <S, T> T matchSourceToTarget(S source, T target) {
        BeanMatch beanMatch = getBeanMatch(source, target);
        for (String fieldName : beanMatch.getTargetNode().keySet()) {
            BeanField sourceField = beanMatch.getSourceNode().get(fieldName);
            BeanField targetField = beanMatch.getTargetNode().get(fieldName);
            processField(new BeanFieldMatch(source, target, sourceField, targetField, fieldName));
        }
        return target;
    }

    private <T, S> BeanMatch getBeanMatch(S source, T target) {
        Class<?> sourceClass = beanUnproxy.unproxy(source.getClass());
        Class<?> targetClass = beanUnproxy.unproxy(target.getClass());
        return beanMatchStore.getBeanMatch(sourceClass, targetClass);
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
        if (!isConverterFor(beanFieldMatch.getSourceClass(), beanFieldMatch.getTargetClass()) && !beanFieldMatch.hasSimilarClasses() && isMappableClass(beanFieldMatch.getTargetClass())) {
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
        Object convertedValue = convert(copyableSource, beanFieldMatch.getTargetClass());
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
        if (encapsulatedSource != null) {
            Object encapsulatedTarget = beanFieldMatch.getOrCreateTargetObject();
            matchSourceToTarget(encapsulatedSource, encapsulatedTarget);
        }
    }

    /**
     * Verifies whether the class is part of the beans which may be mapped by the BeanMapper. This logic is
     * used when nested classes are encountered which need to be treated in a similar way as the main source/
     * target classes.
     * @param clazz the class to be verified against the allowed packages
     * @return true if the class may be mapped, false if it may not
     */
    private boolean isMappableClass(Class<?> clazz) {
        for (Package packagePrefix : packagePrefixesForMappableClasses) {
            if (clazz.getPackage() != null && clazz.getPackage().toString().startsWith(packagePrefix.toString())) {
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
    public Object convert(Object value, Class<?> targetClass) {
        if (value == null) {
            return null;
        }

        Class<?> valueClass = beanUnproxy.unproxy(value.getClass());
        if (targetClass.isAssignableFrom(valueClass)) {
            return value;
        }
        BeanConverter converter = getConverter(valueClass, targetClass);
        return converter.convert(value, targetClass);
    }

    /**
     * Verifies whether a beanConverter is available to apply for conversion
     * @param sourceClass the source class of the conversion
     * @param targetClass the target class of the conversion
     * @return the beanConverter to do the conversion with
     */
    private BeanConverter getConverter(Class<?> sourceClass, Class<?> targetClass) {
        // Register the default converters last so that custom converters are placed before
        if (shouldAddDefaultConverters) {
            addDefaultConverters();
        }
        // Retrieve the first supported converter
        for (BeanConverter converter : beanConverters) {
            if (converter.match(sourceClass, targetClass)) {
                return converter;
            }
        }
        throw new BeanConversionException(sourceClass, targetClass);
    }

    private boolean isConverterFor(Class<?> sourceClass, Class<?> targetClass) {
        try {
            getConverter(sourceClass, targetClass);
            return true;
        } catch (BeanConversionException e) {
            return false;
        }
    }
    
    // Configurations
    
    /**
     * Adds a package on the basis of a class. All classes in that package and sub-packages are
     * eligible for mapping. The root source and target do not need to be set as such, because
     * the verification is only run against nested classes which should be mapped implicity as
     * well
     * @param clazz the class which sets the package prefix for all mappable classes
     */
    public final void addPackagePrefix(Class<?> clazz) {
        packagePrefixesForMappableClasses.add(clazz.getPackage());
    }
    
    /**
     * Add a converter class (must inherit from abstract BeanConverter class) to the beanMapper.
     * On mapping, the beanMapper will check for a suitable converter and use its from and
     * to methods to convert the value of the fields to the correct new data type.
     * @param converter an instance of the class that contains the conversion method implementations and inherits
     *                  from the abstract BeanConverter class.
     */
    public final void addConverter(BeanConverter converter) {
        converter.setBeanMapper(this);
        beanConverters.add(converter);
    }
    
    /**
     * Add classes to skip while unproxying to prevent failing of the BeanMapper while mapping
     * proxy classes or classes containing synthetic fields (Like ENUM types).
     * @param clazz the class that is added to the list of skipped classes
     */
    public final void addProxySkipClass(Class<?> clazz) {
        beanUnproxy.skip(clazz);
    }
    
    /**
     * Add all default converters.
     */
    private void addDefaultConverters() {
        shouldAddDefaultConverters = false;

        addConverter(new PrimitiveConverter());
        addConverter(new StringToBooleanConverter());
        addConverter(new StringToIntegerConverter());
        addConverter(new StringToLongConverter());
        addConverter(new StringToBigDecimalConverter());
        addConverter(new StringToEnumConverter());
        addConverter(new NumberToNumberConverter());
        addConverter(new ObjectToStringConverter());
    }

    public final void setBeanInitializer(BeanInitializer beanInitializer) {
        this.beanInitializer = beanInitializer;
    }
    
    public final void setBeanUnproxy(BeanUnproxy beanUnproxy) {
        this.beanUnproxy.setDelegate(beanUnproxy);
    }

}
