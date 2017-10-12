package io.beanmapper.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.beanmapper.annotations.BeanAlias;
import io.beanmapper.annotations.BeanCollection;
import io.beanmapper.annotations.BeanIgnore;
import io.beanmapper.annotations.BeanProperty;
import io.beanmapper.annotations.BeanUnwrap;
import io.beanmapper.config.BeanPair;
import io.beanmapper.core.converter.collections.BeanCollectionInstructions;
import io.beanmapper.core.inspector.PropertyAccessor;
import io.beanmapper.core.inspector.PropertyAccessors;
import io.beanmapper.exceptions.BeanMissingPathException;
import io.beanmapper.exceptions.BeanNoSuchPropertyException;

public class BeanMatchStore {

    private Map<String, Map<String, BeanMatch>> store = new TreeMap<String, Map<String, BeanMatch>>();

    public void validateStrictBeanPairs(List<BeanPair> beanPairs) {
        List<BeanMatchValidationMessage> validationMessages = new ArrayList<BeanMatchValidationMessage>();
        for (BeanPair beanPair : beanPairs) {
            try {
                getBeanMatch(beanPair);
            } catch (BeanStrictMappingRequirementsException ex) {
                validationMessages.addAll(ex.getValidationMessages());
            }
        }

        if (validationMessages.size() > 0) {
            throw new BeanStrictMappingRequirementsException(validationMessages);
        }
    }

    public BeanMatch getBeanMatch(BeanPair beanPair) {
        Map<String, BeanMatch> targetsForSource = getTargetsForSource(beanPair.getSourceClass());
        if (targetsForSource == null || !containsKeyForTargetClass(targetsForSource, beanPair)) {
            return addBeanMatch(determineBeanMatch(beanPair));
        }
        return getTarget(targetsForSource, beanPair.getTargetClass());
    }

    private boolean containsKeyForTargetClass(Map<String, BeanMatch> targetsForSource, BeanPair beanPair) {
        return targetsForSource.containsKey(beanPair.getTargetClass().getCanonicalName());
    }

    public BeanMatch addBeanMatch(BeanMatch beanMatch) {
        Map<String, BeanMatch> targetsForSource = getTargetsForSource(beanMatch.getSourceClass());
        if (targetsForSource == null) {
            targetsForSource = new TreeMap<String, BeanMatch>();
            store.put(beanMatch.getSourceClass().getCanonicalName(), targetsForSource);
        }
        storeTarget(targetsForSource, beanMatch.getTargetClass(), beanMatch);
        return beanMatch;
    }

    private Map<String, BeanMatch> getTargetsForSource(Class<?> sourceClass) {
        return store.get(sourceClass.getCanonicalName());
    }

    private BeanMatch getTarget(Map<String, BeanMatch> targetsForSource, Class<?> target) {
        return targetsForSource.get(target.getCanonicalName());
    }

    private void storeTarget(Map<String, BeanMatch> targetsForSource, Class<?> target, BeanMatch beanMatch) {
        targetsForSource.put(target.getCanonicalName(), beanMatch);
    }

    private BeanMatch determineBeanMatch(BeanPair beanPair) {
        return determineBeanMatch(beanPair, new TreeMap<String, BeanField>(), new TreeMap<String, BeanField>(), new TreeMap<String, BeanField>());
    }

    private BeanMatch determineBeanMatch(BeanPair beanPair,
                                         Map<String, BeanField> sourceNode, Map<String, BeanField> targetNode, Map<String, BeanField> aliases) {
        return new BeanMatch(
                beanPair,
                getAllFields(
                        sourceNode,
                        targetNode,
                        aliases,
                        beanPair.getSourceClass(),
                        beanPair.getTargetClass(),
                        null,
                        PropertyMatchupDirection.SOURCE_TO_TARGET),
                getAllFields(
                        targetNode,
                        sourceNode,
                        aliases,
                        beanPair.getTargetClass(),
                        beanPair.getSourceClass(),
                        null,
                        PropertyMatchupDirection.TARGET_TO_SOURCE),
                aliases);
    }

    private Map<String, BeanField> getAllFields(Map<String, BeanField> ourNodes, Map<String, BeanField> otherNodes, Map<String, BeanField> aliases, Class<?> ourType, Class<?> otherType, BeanField prefixingBeanField, PropertyMatchupDirection matchupDirection) {

        Map<String, BeanField> ourCurrentNodes = ourNodes;
        List<PropertyAccessor> accessors = PropertyAccessors.getAll(ourType);
        for (PropertyAccessor accessor : accessors) {

            if (!matchupDirection.validAccessor(accessor)) {
                continue;
            }

            // Ignore fields
            if (accessor.findAnnotation(BeanIgnore.class) != null) {
                continue;
            }

            // BeanProperty allows the field to match with a field from the other side with a different name
            // and even a different nesting level.
            String name = dealWithBeanProperty(otherNodes, otherType, accessor);

            // Unwrap the fields which exist in the unwrap class
            BeanField currentBeanField = null;
            try {
                currentBeanField = BeanField.determineNodesForPath(ourType, accessor.getName(), prefixingBeanField);
            } catch (BeanNoSuchPropertyException e) {
                throw new BeanMissingPathException(ourType, accessor.getName(), e);
            }

            handleBeanCollectionAnnotation(accessor.findAnnotation(BeanCollection.class), currentBeanField);

            if(accessor.findAnnotation(BeanAlias.class) != null) {
                BeanAlias beanAlias = accessor.findAnnotation(BeanAlias.class);
                if(aliases.containsKey(beanAlias.value())) {
                    throw new IllegalArgumentException("There is already a BeanAlias with key " + beanAlias.value());
                }
                aliases.put(beanAlias.value(), currentBeanField);
            }

            if (accessor.findAnnotation(BeanUnwrap.class) != null) {
                ourCurrentNodes = getAllFields(
                        ourCurrentNodes,
                        otherNodes,
                        aliases,
                        accessor.getType(),
                        otherType,
                        currentBeanField,
                        matchupDirection);
            } else {
                ourCurrentNodes.put(name, currentBeanField);
            }
        }
        return ourCurrentNodes;
    }

    private void handleBeanCollectionAnnotation(BeanCollection beanCollection, BeanField beanField) {
        if (beanCollection == null) {
            return;
        }
        BeanCollectionInstructions collectionInstructions = new BeanCollectionInstructions();
        collectionInstructions.setCollectionMapsTo(beanCollection.elementType());
        collectionInstructions.setBeanCollectionUsage(beanCollection.beanCollectionUsage());
        collectionInstructions.setPreferredInstantiatedClass(beanCollection.preferredCollectionClass());
        beanField.setCollectionInstructions(collectionInstructions);
    }

    private String dealWithBeanProperty(Map<String, BeanField> otherNodes, Class<?> otherType, PropertyAccessor accessor) {
        String name = accessor.getName();
        if (accessor.findAnnotation(BeanProperty.class) != null) {
            name = accessor.findAnnotation(BeanProperty.class).name();
            // Get the other field from the location that is specified in the beanProperty annotation.
            // If the field is referred to by a path, store the custom field in the other map
            try {
                otherNodes.put(name, BeanField.determineNodesForPath(otherType, name, null));
            } catch (BeanNoSuchPropertyException err) {
                // Acceptable, might have been tagged as @BeanProperty as well
            }
        }
        return name;
    }

}
