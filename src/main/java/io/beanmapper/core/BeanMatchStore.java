package io.beanmapper.core;

import static io.beanmapper.core.converter.collections.AnnotationClass.EMPTY_ANNOTATION_CLASS;
import static io.beanmapper.core.converter.collections.CollectionElementType.EMPTY_COLLECTION_ELEMENT_TYPE;
import static io.beanmapper.core.converter.collections.CollectionElementType.derived;
import static io.beanmapper.core.converter.collections.CollectionElementType.set;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

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
import io.beanmapper.core.inspector.PropertyAccessor;
import io.beanmapper.core.inspector.PropertyAccessors;
import io.beanmapper.core.unproxy.BeanUnproxy;
import io.beanmapper.exceptions.BeanMissingPathException;
import io.beanmapper.exceptions.BeanNoSuchPropertyException;

public class BeanMatchStore {

    private final CollectionHandlerStore collectionHandlerStore;

    private final BeanUnproxy beanUnproxy;

    private final Map<String, Map<String, BeanMatch>> store = new TreeMap<>();

    public BeanMatchStore(CollectionHandlerStore collectionHandlerStore, BeanUnproxy beanUnproxy) {
        this.collectionHandlerStore = collectionHandlerStore;
        this.beanUnproxy = beanUnproxy;
    }

    public void validateStrictBeanPairs(Collection<BeanPair> beanPairs) {
        Collection<BeanMatchValidationMessage> validationMessages = new ArrayList<>();
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
            targetsForSource = new TreeMap<>();
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
        return determineBeanMatch(beanPair, new TreeMap<>(), new TreeMap<>(), new TreeMap<>());
    }

    private BeanMatch determineBeanMatch(BeanPair beanPair,
                                         Map<String, BeanProperty> sourceNode, Map<String, BeanProperty> targetNode, Map<String, BeanProperty> aliases) {
        return new BeanMatch(
                beanPair,
                getAllFields(
                        sourceNode,
                        targetNode,
                        aliases,
                        beanPair.getSourceClass(),
                        beanPair.getTargetClass(),
                        null,
                        BeanPropertyMatchupDirection.SOURCE_TO_TARGET),
                getAllFields(
                        targetNode,
                        sourceNode,
                        aliases,
                        beanPair.getTargetClass(),
                        beanPair.getSourceClass(),
                        null,
                        BeanPropertyMatchupDirection.TARGET_TO_SOURCE),
                aliases);
    }

    private Map<String, BeanProperty> getAllFields(Map<String, BeanProperty> ourNodes, Map<String, BeanProperty> otherNodes, Map<String, BeanProperty> aliases, Class<?> ourType, Class<?> otherType, BeanProperty precedingBeanProperty, BeanPropertyMatchupDirection matchupDirection) {

        Map<String, BeanProperty> ourCurrentNodes = ourNodes;
        Iterable<PropertyAccessor> accessors = PropertyAccessors.getAll(ourType);
        for (PropertyAccessor accessor : accessors) {

            BeanPropertyAccessType accessType = matchupDirection.accessType(accessor);
            if (accessType == BeanPropertyAccessType.NO_ACCESS) {
                continue;
            }

            // Ignore fields
            if (accessor.findAnnotation(BeanIgnore.class) != null) {
                continue;
            }

            // BeanProperty allows the field to match with a field from the other side with a different name
            // and even a different nesting level.
            BeanPropertyWrapper beanPropertyWrapper =
                    dealWithBeanProperty(matchupDirection, otherNodes, otherType, accessor);

            // Unwrap the fields which exist in the unwrap class
            BeanProperty currentBeanProperty = null;
            try {
                currentBeanProperty = new BeanPropertyCreator(
                        matchupDirection, ourType, accessor.getName()).determineNodesForPath(precedingBeanProperty);
                currentBeanProperty.setMustMatch(beanPropertyWrapper.isMustMatch());
            } catch (BeanNoSuchPropertyException e) {
                throw new BeanMissingPathException(ourType, accessor.getName(), e);
            }

            handleBeanCollectionAnnotation(
                    accessor.findAnnotation(BeanCollection.class),
                    currentBeanProperty,
                    matchupDirection);

            handleBeanRoleSecuredAnnotation(
                    currentBeanProperty,
                    accessor.findAnnotation(BeanRoleSecured.class));

            handleBeanLogicSecuredAnnotation(
                    currentBeanProperty,
                    accessor.findAnnotation(BeanLogicSecured.class));

            if(accessor.findAnnotation(BeanAlias.class) != null) {
                BeanAlias beanAlias = accessor.findAnnotation(BeanAlias.class);
                if(aliases.containsKey(beanAlias.value())) {
                    throw new IllegalArgumentException("There is already a BeanAlias with key " + beanAlias.value());
                }
                aliases.put(beanAlias.value(), currentBeanProperty);
            }

            if (accessor.findAnnotation(BeanUnwrap.class) != null) {
                ourCurrentNodes = getAllFields(
                        ourCurrentNodes,
                        otherNodes,
                        aliases,
                        accessor.getType(),
                        otherType,
                        currentBeanProperty,
                        matchupDirection);
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

    private void handleBeanCollectionAnnotation(
            BeanCollection beanCollection,
            BeanProperty beanProperty,
            BeanPropertyMatchupDirection matchupDirection) {

        CollectionElementType elementType = EMPTY_COLLECTION_ELEMENT_TYPE;
        BeanCollectionUsage beanCollectionUsage = null;
        AnnotationClass preferredCollectionClass = EMPTY_ANNOTATION_CLASS;
        Boolean flushAfterClear = null;

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
            Class<?> genericClassOfField =
                    beanProperty.getGenericClassOfField(collectionHandler.getGenericParameterIndex());
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

    private BeanPropertyWrapper dealWithBeanProperty(
            BeanPropertyMatchupDirection matchupDirection, Map<String, BeanProperty> otherNodes,
            Class<?> otherType, PropertyAccessor accessor) {
        BeanPropertyWrapper wrapper = new BeanPropertyWrapper(accessor.getName());
        if (accessor.findAnnotation(io.beanmapper.annotations.BeanProperty.class) != null) {
            wrapper.setMustMatch();
            wrapper.setName(getBeanPropertyName(accessor.findAnnotation(io.beanmapper.annotations.BeanProperty.class)));
            // Get the other field from the location that is specified in the beanProperty annotation.
            // If the field is referred to by a path, store the custom field in the other map
            try {
                otherNodes.put(
                        wrapper.getName(),
                        new BeanPropertyCreator(matchupDirection.getInverse(), otherType, wrapper.getName())
                            .determineNodesForPath());
            } catch (BeanNoSuchPropertyException err) {
                // Acceptable, might have been tagged as @BeanProperty as well
            }
        }
        return wrapper;
    }

    private String getBeanPropertyName(io.beanmapper.annotations.BeanProperty annotation) {
        String beanPropertyName = annotation.value();

        if (beanPropertyName.isEmpty()) {
            beanPropertyName = annotation.name();
        }

        return beanPropertyName;
    }

}
