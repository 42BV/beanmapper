package io.beanmapper.core;

import static io.beanmapper.core.converter.collections.AnnotationClass.EMPTY_ANNOTATION_CLASS;
import static io.beanmapper.core.converter.collections.CollectionElementType.EMPTY_COLLECTION_ELEMENT_TYPE;
import static io.beanmapper.core.converter.collections.CollectionElementType.derived;
import static io.beanmapper.core.converter.collections.CollectionElementType.set;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.beanmapper.annotations.BeanAlias;
import io.beanmapper.annotations.BeanCollection;
import io.beanmapper.annotations.BeanCollectionUsage;
import io.beanmapper.annotations.BeanIgnore;
import io.beanmapper.annotations.BeanLogicSecured;
import io.beanmapper.annotations.BeanRoleSecured;
import io.beanmapper.annotations.BeanUnwrap;
import io.beanmapper.config.BeanPair;
import io.beanmapper.config.CollectionHandlerStore;
import io.beanmapper.core.collections.CollectionHandler;
import io.beanmapper.core.converter.collections.AnnotationClass;
import io.beanmapper.core.converter.collections.BeanCollectionInstructions;
import io.beanmapper.core.converter.collections.CollectionElementType;
import io.beanmapper.core.inspector.BeanPropertySelector;
import io.beanmapper.core.inspector.PropertyAccessor;
import io.beanmapper.core.inspector.PropertyAccessors;
import io.beanmapper.core.unproxy.BeanUnproxy;
import io.beanmapper.exceptions.BeanMissingPathException;
import io.beanmapper.exceptions.BeanNoSuchPropertyException;
import io.beanmapper.utils.Trinary;

public class BeanMatchStore {

    private final CollectionHandlerStore collectionHandlerStore;

    private final BeanUnproxy beanUnproxy;

    private final Map<Class<?>, Map<Class<?>, BeanMatch>> store = new HashMap<>();

    private final BeanPropertySelector beanPropertySelector;

    public BeanMatchStore(CollectionHandlerStore collectionHandlerStore, BeanUnproxy beanUnproxy, BeanPropertySelector beanPropertySelector) {
        this.collectionHandlerStore = collectionHandlerStore;
        this.beanUnproxy = beanUnproxy;
        this.beanPropertySelector = beanPropertySelector;
    }

    public void validateStrictBeanPairs(List<BeanPair> beanPairs) {
        List<BeanMatchValidationMessage> validationMessages = new ArrayList<>();
        for (BeanPair beanPair : beanPairs) {
            try {
                getBeanMatch(beanPair);
            } catch (BeanStrictMappingRequirementsException ex) {
                validationMessages.addAll(ex.getValidationMessages());
            }
        }

        if (!validationMessages.isEmpty()) {
            throw new BeanStrictMappingRequirementsException(validationMessages);
        }
    }

    public BeanMatch getBeanMatch(BeanPair beanPair) {
        Map<Class<?>, BeanMatch> targetsForSource = store.get(beanPair.getSourceClass());

        if (targetsForSource == null) {
            return addBeanMatch(determineBeanMatch(beanPair));
        }

        var beanMatch = targetsForSource.get(beanPair.getTargetClass());

        if (beanMatch == null) {
            return addBeanMatch(determineBeanMatch(beanPair));
        }

        return beanMatch;
    }

    public BeanMatch addBeanMatch(BeanMatch beanMatch) {
        Map<Class<?>, BeanMatch> targetsForSource = store.computeIfAbsent(beanMatch.getSourceClass(), k -> new HashMap<>());
        targetsForSource.put(beanMatch.getTargetClass(), beanMatch);
        return beanMatch;
    }

    private BeanMatch determineBeanMatch(BeanPair beanPair) {

        Map<String, BeanProperty> sourceNode = new HashMap<>();
        Map<String, BeanProperty> targetNode = new HashMap<>();
        Map<String, BeanProperty> aliases = new HashMap<>();

        return new BeanMatch(
                beanPair,
                getAllFields(
                        sourceNode,
                        targetNode,
                        aliases,
                        beanPair.getSourceClass(),
                        beanPair.getTargetClass(),
                        null,

                        BeanPropertyMatchupDirection.SOURCE_TO_TARGET
                            ),
                getAllFields(
                        targetNode,
                        sourceNode,
                        aliases,
                        beanPair.getTargetClass(),
                        beanPair.getSourceClass(),
                        null,

                        BeanPropertyMatchupDirection.TARGET_TO_SOURCE
                            ),
                aliases
        );
    }

    private Map<String, BeanProperty> getAllFields(Map<String, BeanProperty> ourNodes, Map<String, BeanProperty> otherNodes, Map<String, BeanProperty> aliases,
            Class<?> ourType, Class<?> otherType, BeanProperty precedingBeanProperty, BeanPropertyMatchupDirection matchupDirection) {

        Map<String, BeanProperty> ourCurrentNodes = ourNodes;
        List<PropertyAccessor> accessors = PropertyAccessors.getAll(ourType);
        for (PropertyAccessor accessor : accessors) {

            BeanPropertyAccessType accessType = matchupDirection.accessType(accessor);
            if (accessType == BeanPropertyAccessType.NO_ACCESS || accessor.isAnnotationPresent(BeanIgnore.class)) {
                continue;
            }

            // BeanProperty allows the field to match with a field from the other side with a different name
            // and even a different nesting level.
            BeanPropertyWrapper beanPropertyWrapper = beanPropertySelector.dealWithBeanProperty(matchupDirection, otherNodes, otherType, accessor);

            // Unwrap the fields which exist in the unwrap class
            BeanProperty currentBeanProperty;
            try {
                currentBeanProperty = new BeanPropertyCreator(matchupDirection, ourType, accessor.getName()).determineNodesForPath(precedingBeanProperty);
                currentBeanProperty.setMustMatch(beanPropertyWrapper.isMustMatch());
            } catch (BeanNoSuchPropertyException e) {
                throw new BeanMissingPathException(ourType, accessor.getName(), e);
            }

            handleBeanCollectionAnnotation(accessor.findAnnotation(BeanCollection.class), currentBeanProperty, matchupDirection);

            handleBeanRoleSecuredAnnotation(currentBeanProperty, accessor.findAnnotation(BeanRoleSecured.class));

            handleBeanLogicSecuredAnnotation(currentBeanProperty, accessor.findAnnotation(BeanLogicSecured.class));

            if (accessor.isAnnotationPresent(BeanAlias.class)) {
                BeanAlias beanAlias = accessor.findAnnotation(BeanAlias.class);
                if (aliases.containsKey(beanAlias.value())) {
                    throw new IllegalArgumentException("There is already a BeanAlias with key " + beanAlias.value());
                }
                aliases.put(beanAlias.value(), currentBeanProperty);
            }

            if (accessor.isAnnotationPresent(BeanUnwrap.class)) {
                ourCurrentNodes = getAllFields(ourCurrentNodes, otherNodes, aliases, accessor.getType(), otherType, currentBeanProperty, matchupDirection);
            } else {
                ourCurrentNodes.put(beanPropertyWrapper.getName(), currentBeanProperty);
            }
        }
        return ourCurrentNodes;
    }

    private void handleBeanLogicSecuredAnnotation(BeanProperty beanProperty, BeanLogicSecured beanLogicSecured) {
        if (beanLogicSecured == null) {
            return;
        }
        beanProperty.setLogicSecuredCheck(beanLogicSecured.value());
    }

    private void handleBeanRoleSecuredAnnotation(BeanProperty beanProperty, BeanRoleSecured beanRoleSecured) {
        if (beanRoleSecured == null) {
            return;
        }
        beanProperty.setRequiredRoles(beanRoleSecured.value());
    }

    private void handleBeanCollectionAnnotation(BeanCollection beanCollection, BeanProperty beanProperty, BeanPropertyMatchupDirection matchupDirection) {

        CollectionElementType elementType = EMPTY_COLLECTION_ELEMENT_TYPE;
        BeanCollectionUsage beanCollectionUsage = null;
        AnnotationClass preferredCollectionClass = EMPTY_ANNOTATION_CLASS;
        Trinary flushAfterClear = Trinary.UNSET;

        CollectionHandler collectionHandler = null;
        if (beanCollection == null) {
            if (collectionHandlerStore == null) { // probably Class generation -- not required
                return;
            }
            if (!matchupDirection.checkFieldForCollectionProperty()) { // Wrong side -- ignore
                return;
            }
            collectionHandler = getCollectionHandlerFor(beanProperty);
            if (collectionHandler == null) { // Not a collection, so not interesting
                return;
            }
        } else {
            elementType = set(beanCollection.elementType());
            beanCollectionUsage = beanCollection.beanCollectionUsage();
            preferredCollectionClass = new AnnotationClass(beanCollection.preferredCollectionClass());
            flushAfterClear = beanCollection.flushAfterClear();
        }

        if (elementType.isEmpty()) {
            if (collectionHandler == null) {
                collectionHandler = getCollectionHandlerFor(beanProperty);
            }
            Class<?> genericClassOfField = beanProperty.getGenericClassOfField(collectionHandler.getGenericParameterIndex());
            if (genericClassOfField != null) {
                elementType = derived(genericClassOfField);
            }
        }

        BeanCollectionInstructions collectionInstructions = new BeanCollectionInstructions();
        collectionInstructions.setCollectionElementType(elementType);
        collectionInstructions.setBeanCollectionUsage(beanCollectionUsage);
        collectionInstructions.setPreferredCollectionClass(preferredCollectionClass);
        collectionInstructions.setFlushAfterClear(flushAfterClear);
        beanProperty.setCollectionInstructions(collectionInstructions);
    }

    private CollectionHandler getCollectionHandlerFor(BeanProperty beanProperty) {
        return collectionHandlerStore.getCollectionHandlerFor(beanProperty.getAccessor().getType(), beanUnproxy);
    }
}
