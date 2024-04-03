package io.beanmapper.strategy;

import io.beanmapper.BeanMapper;
import io.beanmapper.annotations.BeanConstruct;
import io.beanmapper.annotations.BeanDefault;
import io.beanmapper.annotations.BeanMappableEnum;
import io.beanmapper.annotations.BeanParent;
import io.beanmapper.annotations.BeanProperty;
import io.beanmapper.config.Configuration;
import io.beanmapper.core.BeanMatch;
import io.beanmapper.core.BeanPropertyMatch;
import io.beanmapper.core.converter.BeanConverter;
import io.beanmapper.core.unproxy.BeanUnproxy;
import io.beanmapper.core.unproxy.UnproxyResultStore;
import io.beanmapper.exceptions.BeanConversionException;
import io.beanmapper.exceptions.BeanPropertyNoMatchException;
import io.beanmapper.utils.BeanMapperTraceLogger;
import io.beanmapper.utils.Records;

public abstract class AbstractMapStrategy implements MapStrategy {

    private static final String INDENT = "    ";

    private static final String ARROW = " -> ";

    private final BeanMapper beanMapper;
    private final Configuration configuration;

    protected AbstractMapStrategy(BeanMapper beanMapper, Configuration configuration) {
        this.beanMapper = beanMapper;
        this.configuration = configuration;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public BeanMapper getBeanMapper() {
        return beanMapper;
    }

    /**
     * Composes the {@link ConstructorArguments}-object for the given source and {@link BeanMatch}-object.
     *
     * <p>This method is used specifically by the {@link MapToClassStrategy} and the {@link MapToRecordStrategy}.</p><p>The source-class is checked for the
     * presence of the {@link BeanConstruct}-annotation. If the annotation is present, the names of the fields described by the annotation will be stored in the
     * values-array of the method. <p>If no BeanConstruct-annotation is present, a check will be performed to see if the target-class is a record. If so, the
     * values-array will be set to the result of the {@link Records#getRecordFieldNames(Class)}-method, which essentially loops through all the
     * RecordComponent-objects associated with the target-class, and returns an array of their names.</p><p>Lastly, if the values-array is null, null will
     * be returned. Otherwise, a new ConstructorArguments-object will be returned, using the source-object, the BeanMatch, and the values-array.</p>
     *
     * @param source The source-object that will be mapped to the target.
     * @param beanMatch The BeanMatch that will be used to map the source to the target.
     * @return ConstructorArguments-object constructed from the source, beanMatch and values taken from either the BeanConstruct-annotation, or
     *         RecordComponents.
     * @param <S> The type of the source-object.
     */
    public <S> ConstructorArguments getConstructorArguments(S source, BeanMatch beanMatch) {
        final Class<?> targetClass = beanMatch.getTargetClass();

        BeanConstruct beanConstruct = targetClass.isAnnotationPresent(BeanConstruct.class)
                ? targetClass.getAnnotation(BeanConstruct.class)
                : beanMatch.getSourceClass().getAnnotation(BeanConstruct.class);

        // If no BeanConstruct exists, assume a no-arg constructor must be used
        return beanConstruct == null ? null : new ConstructorArguments(source, beanMatch, beanConstruct.value());
    }

    public <T, S> BeanMatch getBeanMatch(Class<S> sourceClazz, Class<T> targetClazz) {
        BeanUnproxy unproxy = getConfiguration().getBeanUnproxy();
        Class<?> sourceClass = UnproxyResultStore.getInstance().getOrComputeUnproxyResult(sourceClazz, unproxy);
        Class<?> targetClass = UnproxyResultStore.getInstance().getOrComputeUnproxyResult(targetClazz, unproxy);
        return getConfiguration().getBeanMatchStore().getBeanMatch(
                configuration.getStrictMappingProperties().createBeanPair(sourceClass, targetClass)
        );
    }

    /**
     * The copy action puts the source's value to the target. When @BeanDefault has been set and the
     * value to copy is empty, it will use the default.
     * @param beanPropertyMatch contains the fields belonging to the source/target field match
     */
    private void copySourceToTarget(BeanPropertyMatch beanPropertyMatch) {
        Object copyableSource = beanPropertyMatch.getSourceObject();

        if (copyableSource == null) {
            if (beanPropertyMatch.targetHasAnnotation(BeanDefault.class)) {
                copyableSource = beanPropertyMatch.getTargetDefaultValue();
            } else if (beanPropertyMatch.sourceHasAnnotation(BeanDefault.class)) {
                copyableSource = beanPropertyMatch.getSourceDefaultValue();
            }
        }

        final Object convertedValue;
        if (beanPropertyMatch.sourceHasAnnotation(BeanParent.class) || beanPropertyMatch.targetHasAnnotation(BeanParent.class)) {
            convertedValue = beanMapper.getConfiguration().getParent();
        } else {
            convertedValue = convert(copyableSource, beanPropertyMatch.getTargetClass(), beanPropertyMatch);
        }

        beanPropertyMatch.writeObject(convertedValue);
    }

    /**
     * If the field is a class which can itself be mapped to another class, it must be treated
     * as such. The matching process is called recursively to deal with this pair.
     * @param beanPropertyMatch contains the fields belonging to the source/target field match
     */
    private void dealWithMappableNestedClass(BeanPropertyMatch beanPropertyMatch) {
        Object encapsulatedSource = beanPropertyMatch.getSourceObject();
        Object target;
        if (encapsulatedSource != null) {
            BeanMapperTraceLogger.log("    {");
            BeanMapper localBeanMapper = getBeanMapper()
                    .wrap()
                    .setParent(beanPropertyMatch.getTarget())
                    .build();
            if (beanPropertyMatch.getTargetObject() == null) {
                target = localBeanMapper.map(encapsulatedSource, beanPropertyMatch.getTargetClass());
            } else {
                target = localBeanMapper.map(encapsulatedSource, beanPropertyMatch.getTargetObject());
            }
            beanPropertyMatch.writeObject(target);
            BeanMapperTraceLogger.log("    }");
        }
    }

    /**
     * Converts a value into the target class.
     * @param value the value to convert
     * @param targetClass the target class
     * @param beanPropertyMatch contains the fields belonging to the source/target field match
     * @return the converted value
     */
    public Object convert(Object value, Class<?> targetClass, BeanPropertyMatch beanPropertyMatch) {

        BeanUnproxy unproxy = getConfiguration().getBeanUnproxy();
        Class<?> valueClass = UnproxyResultStore.getInstance().getOrComputeUnproxyResult(beanPropertyMatch.getSourceClass(), unproxy);
        BeanConverter converter = getConverterOptional(valueClass, targetClass);

        if (converter != null) {
            BeanMapperTraceLogger.log("{}{}{}", INDENT, converter.getClass().getSimpleName(), ARROW);
            BeanMapper wrappedBeanMapper = beanMapper
                    .wrap()
                    .setParent(beanPropertyMatch.getTarget())
                    .build();
            return converter.convert(wrappedBeanMapper, value, targetClass, beanPropertyMatch);
        }

        if (value == null) {
            return this.configuration.getDefaultValueForClass(targetClass);
        }

        if (targetClass.isAssignableFrom(valueClass)) {
            return value;
        }

        throw new BeanConversionException(beanPropertyMatch.getSourceClass(), targetClass);
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
     * @param beanMatch the matchup of source and target
     * @return A filled target object.
     */
    public <S, T> T processProperties(S source, T target, BeanMatch beanMatch) {
        for (String fieldName : beanMatch.getTargetNodes().keySet()) {
            processProperty(new BeanPropertyMatch(
                    source,
                    target,
                    beanMatch.findBeanPairField(fieldName),
                    fieldName,
                    beanMatch));
        }
        beanMatch.checkForMandatoryUnmatchedNodes();
        return target;
    }

    /**
     * Process a single combination of a source and a target field.
     * @param beanPropertyMatch contains the fields belonging to the source/target field match
     */
    private void processProperty(BeanPropertyMatch beanPropertyMatch) {
        if (!beanPropertyMatch.hasMatchingSource()) {
            dealWithNonMatchingNode(beanPropertyMatch);
            return;
        }

        if (!beanPropertyMatch.hasAccess(
                configuration.getRoleSecuredCheck(),
                configuration.getLogicSecuredChecks(),
                configuration.getEnforceSecuredProperties())) {
            return;
        }

        if (noConverterAvailable(beanPropertyMatch) &&
                dissimilarOrSimilarWithExistingTarget(beanPropertyMatch) &&
                neitherSourceNorTargetIsEnum(beanPropertyMatch) &&
                beanMapperMayDeepMapClass(beanPropertyMatch) &&
                thereIsASourceClassToMap(beanPropertyMatch)) {

            dealWithMappableNestedClass(beanPropertyMatch);
            return;
        }
        if (beanPropertyMatch.isMappable()) {
            BeanMapperTraceLogger.log("{}{}", beanPropertyMatch.sourceToString(), ARROW);
            copySourceToTarget(beanPropertyMatch);
            BeanMapperTraceLogger.log("{}{}", INDENT, beanPropertyMatch.targetToString());
        }
    }

    private boolean noConverterAvailable(BeanPropertyMatch beanPropertyMatch) {
        return !isConverterFor(
                beanPropertyMatch.getSourceClass(),
                beanPropertyMatch.getTargetClass());
    }

    private boolean dissimilarOrSimilarWithExistingTarget(BeanPropertyMatch beanPropertyMatch) {
        return !beanPropertyMatch.hasSimilarClasses() ||
                (beanPropertyMatch.hasSimilarClasses() && beanPropertyMatch.getTargetObject() != null);
    }

    private boolean neitherSourceNorTargetIsEnum(BeanPropertyMatch beanPropertyMatch) {
        return !((beanPropertyMatch.getSourceClass().isEnum() && !beanPropertyMatch.getSourceClass().isAnnotationPresent(BeanMappableEnum.class)) ||
                beanPropertyMatch.getTargetClass().isEnum());
    }

    private boolean beanMapperMayDeepMapClass(BeanPropertyMatch beanPropertyMatch) {
        return isMappableClass(beanPropertyMatch.getTargetClass());
    }

    private boolean thereIsASourceClassToMap(BeanPropertyMatch beanPropertyMatch) {
        return beanPropertyMatch.getSourceObject() != null;
    }

    /**
     * This method is run when there is no matching source field for a target field. The result
     * could be that a default is set, or an exception is thrown when a BeanProperty has been set.
     * @param beanPropertyMatch contains the fields belonging to the source/target field match
     */
    private void dealWithNonMatchingNode(BeanPropertyMatch beanPropertyMatch) {
        if (beanPropertyMatch.targetHasAnnotation(BeanDefault.class)) {
            beanPropertyMatch.setTarget(beanPropertyMatch.getTargetDefaultValue());
        } else if (beanPropertyMatch.targetHasAnnotation(BeanProperty.class)) {
            throw new BeanPropertyNoMatchException(beanPropertyMatch.getTargetClass(), beanPropertyMatch.getTargetFieldName());
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
        for (String packagePrefix : getConfiguration().getPackagePrefixes()) {
            if (packageName.startsWith(packagePrefix)) {
                return true;
            }
        }
        return false;
    }

    public BeanConverter getConverterOptional(Class<?> sourceClass, Class<?> targetClass) {

        // Retrieve the first supported converter
        for (BeanConverter beanConverter : getConfiguration().getBeanConverters()) {
            if (beanConverter != null && beanConverter.match(sourceClass, targetClass)) {
                return beanConverter;
            }
        }

        return null;
    }

    private boolean isConverterFor(Class<?> sourceClass, Class<?> targetClass) {
        return getConverterOptional(sourceClass, targetClass) != null;
    }

}
