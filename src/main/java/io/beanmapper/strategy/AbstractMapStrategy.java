package io.beanmapper.strategy;

import io.beanmapper.annotations.BeanParent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.beanmapper.BeanMapper;
import io.beanmapper.annotations.BeanConstruct;
import io.beanmapper.annotations.BeanDefault;
import io.beanmapper.annotations.BeanProperty;
import io.beanmapper.config.Configuration;
import io.beanmapper.core.BeanField;
import io.beanmapper.core.BeanFieldMatch;
import io.beanmapper.core.BeanMatch;
import io.beanmapper.core.converter.BeanConverter;
import io.beanmapper.exceptions.BeanConversionException;
import io.beanmapper.exceptions.BeanFieldNoMatchException;
import io.beanmapper.exceptions.BeanMappingException;
import io.beanmapper.utils.ConstructorArguments;

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
        return getConfiguration().getBeanMatchStore().getBeanMatch(sourceClass, targetClass);
    }

    /**
     * The copy action puts the source's value to the target. When @BeanDefault has been set and the
     * value to copy is empty, it will use the default.
     * @param beanFieldMatch contains the fields belonging to the source/target field match
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

        final Object convertedValue;
        if (beanFieldMatch.sourceHasAnnotation(BeanParent.class) || beanFieldMatch.targetHasAnnotation(BeanParent.class)) {
            convertedValue = beanMapper.getConfiguration().getParent();
        } else {
            convertedValue = convert(copyableSource, beanFieldMatch.getTargetClass(), beanFieldMatch);
        }

        beanFieldMatch.writeObject(convertedValue);
    }

    /**
     * If the field is a class which can itself be mapped to another class, it must be treated
     * as such. The matching process is called recursively to deal with this pair.
     * @param beanFieldMatch contains the fields belonging to the source/target field match
     */
    private void dealWithMappableNestedClass(BeanFieldMatch beanFieldMatch) {
        Object encapsulatedSource = beanFieldMatch.getSourceObject();
        Object target;
        if (encapsulatedSource != null) {
            logger.debug("    {");
            BeanMapper beanMapper = getBeanMapper()
                    .wrapConfig()
                    .setParent(beanFieldMatch.getTarget())
                    .build();
            if(beanFieldMatch.getTargetObject() == null){
                target = beanMapper.map(encapsulatedSource, beanFieldMatch.getTargetClass());
            } else {
                target = beanMapper.map(encapsulatedSource, beanFieldMatch.getTargetObject());
            }
            beanFieldMatch.writeObject(target);
            logger.debug("    }");
        }
    }

    /**
     * Converts a value into the target class.
     * @param value the value to convert
     * @param targetClass the target class
     * @param beanFieldMatch contains the fields belonging to the source/target field match
     * @return the converted value
     */
    @SuppressWarnings("unchecked")
    public Object convert(Object value, Class<?> targetClass, BeanFieldMatch beanFieldMatch) {
        if (value == null) {
            return null;
        }

        Class<?> valueClass = getConfiguration().getBeanUnproxy().unproxy(value.getClass());

        BeanConverter converter = getConverterOptional(valueClass, targetClass);
        if (converter != null) {
            logger.debug(INDENT + converter.getClass().getSimpleName() + ARROW);
            BeanMapper wrappedBeanMapper = beanMapper
                    .wrapConfig()
                    .setParent(beanFieldMatch.getTarget())
                    .build();
            return converter.convert(wrappedBeanMapper, value, targetClass, beanFieldMatch);
        }

        if (targetClass.isAssignableFrom(valueClass)) {
            return value;
        }

        throw new BeanConversionException(beanFieldMatch.getSourceClass(), targetClass);
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
    public <S, T> T processFields(S source, T target, BeanMatch beanMatch) {
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
            logger.debug(beanFieldMatch.sourceToString() + ARROW);
            copySourceToTarget(beanFieldMatch);
            logger.debug(INDENT + beanFieldMatch.targetToString());
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
