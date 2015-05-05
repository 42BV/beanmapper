package io.beanmapper;

import io.beanmapper.annotations.BeanDefault;
import io.beanmapper.annotations.BeanProperty;
import io.beanmapper.core.BeanFieldMatch;
import io.beanmapper.core.BeanMatch;
import io.beanmapper.core.BeanMatchStore;
import io.beanmapper.exceptions.BeanFieldNoMatchException;
import io.beanmapper.exceptions.BeanInstantiationException;
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
     * contains a store of matches for source and target class pairs. A pair is created only
     * once and reused every time thereafter.
     */
    private BeanMatchStore beanMatchStore = new BeanMatchStore();

    /**
     * the list of packages (and subpackages) containing classes which are eligible for mapping.
     */
    private List<Package> packagePrefixesForMappableClasses = new ArrayList<>();

    /**
     * Adds a package on the basis of a class. All classes in that package and sub-packages are
     * eligible for mapping. The root source and target do not need to be set as such, because
     * the verification is only run against nested classes which should be mapped implicity as
     * well
     * @param clazz the class which sets the package prefix for all mappable classes
     */
    public void addPackagePrefix(Class clazz) {
        packagePrefixesForMappableClasses.add(clazz.getPackage());
    }

    /**
     * Copies the values from the source object to a newly constructed target instance
     * @param source source instance of the properties
     * @param targetClass class of the target, needs to be constructed as the target instance
     * @param <S> The instance from which the properties get copied
     * @param <T> the instance to which the properties get copied
     * @return the target instance containing all applicable properties
     * @throws Exception
     */
    public <S, T> T map(S source, Class<T> targetClass) throws BeanMappingException {
        try {
            return map(source, targetClass.getConstructor().newInstance());
        } catch (Exception e) {
            throw new BeanInstantiationException(targetClass, e);
        }
    }

    /**
     * Maps a list of source items to a list of target items with a specific class
     * @param sourceItems the items to be mapped
     * @param targetClass the class type of the items in the returned list
     * @param <S> source
     * @param <T> target
     * @return the list of mapped items with class T
     * @throws Exception
     */
    public <S, T> Collection<T> map(Collection<S> sourceItems, Class<T> targetClass) throws BeanMappingException {
        Collection<T> targetItems = null;
        try {
            targetItems = (Collection<T>)sourceItems.getClass().getConstructor().newInstance();
        } catch (Exception e) {
            throw new BeanInstantiationException(sourceItems.getClass(), e);
        }
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
     * @throws Exception
     */
    public <S, T> T map(S source, T target) throws BeanMappingException {
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
     * @throws Exception
     */
    private <S, T> T matchSourceToTarget (S source, T target) throws BeanMappingException {

        BeanMatch beanMatch = beanMatchStore.getBeanMatch(source.getClass(), target.getClass());

        for (String targetFieldName : beanMatch.getTargetNode().keySet()) {
            processField(new BeanFieldMatch<>(
                    source,
                    target,
                    beanMatch.getSourceNode().get(targetFieldName),
                    beanMatch.getTargetNode().get(targetFieldName),
                    targetFieldName));
        }
        return target;
    }

    /**
     * Process a single combination of a source and a target field.
     * @param beanFieldMatch contains the fields belonging to the source/target field match
     * @throws Exception
     */
    private void processField(BeanFieldMatch beanFieldMatch) throws BeanMappingException {

        if (!beanFieldMatch.hasMatchingSource()) {
            dealWithNonMatchingNode(beanFieldMatch);
            return;
        }

        if (!beanFieldMatch.hasSimilarClasses() && isMappableClass(beanFieldMatch.getTargetClass())) {
            dealWithMappableNestedClass(beanFieldMatch);
            return;
        }

        copySourceToTarget(beanFieldMatch);
    }

    /**
     * This method is run when there is no matching source field for a target field. The result
     * could be that a default is set, or an exception is thrown when a BeanProperty has been set.
     * @param beanFieldMatch contains the fields belonging to the source/target field match
     * @throws IllegalAccessException
     * @throws BeanMappingException
     */
    private void dealWithNonMatchingNode(BeanFieldMatch beanFieldMatch)
            throws BeanMappingException {
        if (beanFieldMatch.targetHasAnnotation(BeanDefault.class)) {
            beanFieldMatch.setTarget(beanFieldMatch.getTargetDefaultValue());
        } else if (beanFieldMatch.targetHasAnnotation(BeanProperty.class)) {
            throw new BeanFieldNoMatchException(
                    "No source field found while attempting to map to " + beanFieldMatch.getTargetFieldName());
        }
    }

    /**
     * The copy action puts the source's value to the target. When @BeanDefault has been set and the
     * value to copy is empty, it will use the default.
     * @param beanFieldMatch contains the fields belonging to the source/target field match
     * @throws Exception
     */
    private void copySourceToTarget(BeanFieldMatch beanFieldMatch) throws BeanMappingException {
        Object copyableSource = beanFieldMatch.getSourceValue();

        if (copyableSource == null) {
            if (beanFieldMatch.targetHasAnnotation(BeanDefault.class)) {
                copyableSource = beanFieldMatch.getTargetDefaultValue();
            } else if (beanFieldMatch.sourceHasAnnotation(BeanDefault.class)) {
                copyableSource = beanFieldMatch.getSourceDefaultValue();
            }
        }

        beanFieldMatch.writeObject(copyableSource);
    }

    /**
     * If the field is a class which can itself be mapped to another class, it must be treated
     * as such. The matching process is called recursively to deal with this pair.
     * @param beanFieldMatch contains the fields belonging to the source/target field match
     * @throws Exception
     */
    private void dealWithMappableNestedClass(BeanFieldMatch beanFieldMatch) throws BeanMappingException {

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
    private boolean isMappableClass(Class clazz) {
        for (Package packagePrefix : packagePrefixesForMappableClasses) {
            if (clazz.getPackage() != null && clazz.getPackage().toString().startsWith(packagePrefix.toString())) {
                return true;
            }
        }
        return false;
    }

}
