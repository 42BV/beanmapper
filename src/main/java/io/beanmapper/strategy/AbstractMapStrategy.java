package io.beanmapper.strategy;

import io.beanmapper.BeanMapper;
import io.beanmapper.annotations.BeanConstruct;
import io.beanmapper.annotations.BeanDefault;
import io.beanmapper.annotations.BeanParent;
import io.beanmapper.annotations.BeanProperty;
import io.beanmapper.config.Configuration;
import io.beanmapper.core.BeanMatch;
import io.beanmapper.core.BeanPropertyMatch;
import io.beanmapper.core.converter.BeanConverter;
import io.beanmapper.core.converter.collections.CollectionConverter;
import io.beanmapper.exceptions.BeanConversionException;
import io.beanmapper.exceptions.BeanPropertyNoMatchException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMapStrategy implements MapStrategy {

    private static final String INDENT = "    ";

    private static final String ARROW = " -> ";

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final BeanMapper beanMapper;
    private final Configuration configuration;

    public AbstractMapStrategy(BeanMapper beanMapper, Configuration configuration) {
        this.beanMapper = beanMapper;
        this.configuration = configuration;
    }

    public Configuration getConfiguration() {
        return configuration;
    }
    
    public BeanMapper getBeanMapper() {
        return beanMapper;
    } 
    
    public <S> ConstructorArguments getConstructorArguments(S source, BeanMatch beanMatch) {
        BeanConstruct beanConstruct = beanMatch.getTargetClass().getAnnotation(BeanConstruct.class);

        // If the target does not have a BeanConstruct annotation, check the source
        if(beanConstruct == null){
            beanConstruct = beanMatch.getSourceClass().getAnnotation(BeanConstruct.class);
        }

        // If no BeanConstruct exists, assume a no-arg constructor must be used
        return beanConstruct == null ? null : new ConstructorArguments(source, beanMatch, beanConstruct.value());
    }

    public<T, S> BeanMatch getBeanMatch(Class<S> sourceClazz, Class<T> targetClazz) {
        Class<?> sourceClass = getConfiguration().getBeanUnproxy().unproxy(sourceClazz);
        Class<?> targetClass = getConfiguration().getBeanUnproxy().unproxy(targetClazz);
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
            logger.debug("    {");
            BeanMapper beanMapper = getBeanMapper()
                    .wrap()
                    .setParent(beanPropertyMatch.getTarget())
                    .build();
            if(beanPropertyMatch.getTargetObject() == null){
                target = beanMapper.map(encapsulatedSource, beanPropertyMatch.getTargetClass());
            } else {
                target = beanMapper.map(encapsulatedSource, beanPropertyMatch.getTargetObject());
            }
            beanPropertyMatch.writeObject(target);
            logger.debug("    }");
        }
    }

    /**
     * Converts a value into the target class.
     * @param value the value to convert
     * @param targetClass the target class
     * @param beanPropertyMatch contains the fields belonging to the source/target field match
     * @return the converted value
     */
    @SuppressWarnings("unchecked")
    public Object convert(Object value, Class<?> targetClass, BeanPropertyMatch beanPropertyMatch) {

        Class<?> valueClass = getConfiguration().getBeanUnproxy().unproxy(beanPropertyMatch.getSourceClass());
        BeanConverter converter = getConverterOptional(valueClass, targetClass);

        // @TODO Consider removing the null check here and offering a null value to BeanConverters as well
        if (value == null && !(converter instanceof CollectionConverter)) {
            return null;
        }

        if (converter != null) {
            logger.debug(INDENT + converter.getClass().getSimpleName() + ARROW);
            BeanMapper wrappedBeanMapper = beanMapper
                    .wrap()
                    .setParent(beanPropertyMatch.getTarget())
                    .build();
            return converter.convert(wrappedBeanMapper, value, targetClass, beanPropertyMatch);
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

        if (    noConverterAvailable(beanPropertyMatch) &&
                dissimilarOrSimilarWithExistingTarget(beanPropertyMatch) &&
                neitherSourceNorTargetIsEnum(beanPropertyMatch) &&
                beanMapperMayDeepMapClass(beanPropertyMatch) &&
                thereIsASourceClassToMap(beanPropertyMatch)) {

            dealWithMappableNestedClass(beanPropertyMatch);
            return;
        }
        if (beanPropertyMatch.isMappable()) {
            logger.debug(beanPropertyMatch.sourceToString() + ARROW);
            copySourceToTarget(beanPropertyMatch);
            logger.debug(INDENT + beanPropertyMatch.targetToString());
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
        return !(beanPropertyMatch.getSourceClass().isEnum() ||
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
