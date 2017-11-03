package io.beanmapper.core;

import static io.beanmapper.core.converter.collections.AnnotationClass.EMPTY_ANNOTATION_CLASS;
import static io.beanmapper.core.converter.collections.CollectionElementType.EMPTY_COLLECTION_ELEMENT_TYPE;
import static io.beanmapper.core.converter.collections.CollectionElementType.set;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.beanmapper.annotations.BeanAlias;
import io.beanmapper.annotations.BeanCollection;
import io.beanmapper.annotations.BeanCollectionUsage;
import io.beanmapper.annotations.BeanIgnore;
import io.beanmapper.annotations.BeanProperty;
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
import io.beanmapper.utils.GenericTypeDeterminer;

public class BeanMatchStore {

    private final CollectionHandlerStore collectionHandlerStore;

    private final BeanUnproxy beanUnproxy;

    private Map<String, Map<String, BeanMatch>> store = new TreeMap<String, Map<String, BeanMatch>>();

    public BeanMatchStore(CollectionHandlerStore collectionHandlerStore, BeanUnproxy beanUnproxy) {
        this.collectionHandlerStore = collectionHandlerStore;
        this.beanUnproxy = beanUnproxy;
    }

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
        BeanMatch beanMatch = new BeanMatch(
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
        return beanMatch;
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
            BeanPropertyWrapper beanPropertyWrapper = dealWithBeanProperty(otherNodes, otherType, accessor);

            // Unwrap the fields which exist in the unwrap class
            BeanField currentBeanField = null;
            try {
                currentBeanField = BeanField.determineNodesForPath(ourType, accessor.getName(), prefixingBeanField);
                currentBeanField.setMustMatch(beanPropertyWrapper.isMustMatch());
            } catch (BeanNoSuchPropertyException e) {
                throw new BeanMissingPathException(ourType, accessor.getName(), e);
            }

            handleBeanCollectionAnnotation(
                    accessor.findAnnotation(BeanCollection.class),
                    ourType,
                    currentBeanField,
                    matchupDirection);

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
                ourCurrentNodes.put(beanPropertyWrapper.getName(), currentBeanField);
            }
        }
        return ourCurrentNodes;
    }

    private void handleBeanCollectionAnnotation(
            BeanCollection beanCollection,
            Class<?> containingClass,
            BeanField beanField,
            PropertyMatchupDirection matchupDirection) {

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
            collectionHandler = getCollectionHandlerFor(beanField);
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
                collectionHandler = getCollectionHandlerFor(beanField);
            }
            elementType = new GenericTypeDeterminer(
                    collectionHandler,
                    containingClass,
                    beanField.getProperty().getName()).determineGenericType();
        }

        BeanCollectionInstructions collectionInstructions = new BeanCollectionInstructions();
        collectionInstructions.setCollectionElementType(elementType);
        collectionInstructions.setBeanCollectionUsage(beanCollectionUsage);
        collectionInstructions.setPreferredCollectionClass(preferredCollectionClass);
        collectionInstructions.setFlushAfterClear(flushAfterClear);
        beanField.setCollectionInstructions(collectionInstructions);
    }

    private CollectionHandler getCollectionHandlerFor(BeanField beanField) {
        return collectionHandlerStore.getCollectionHandlerFor(beanField.getProperty().getType(), beanUnproxy);
    }

    private BeanPropertyWrapper dealWithBeanProperty(Map<String, BeanField> otherNodes, Class<?> otherType, PropertyAccessor accessor) {
        BeanPropertyWrapper wrapper = new BeanPropertyWrapper(accessor.getName());
        if (accessor.findAnnotation(BeanProperty.class) != null) {
            wrapper.setMustMatch();
            wrapper.setName(accessor.findAnnotation(BeanProperty.class).name());
            // Get the other field from the location that is specified in the beanProperty annotation.
            // If the field is referred to by a path, store the custom field in the other map
            try {
                otherNodes.put(
                        wrapper.getName(),
                        BeanField.determineNodesForPath(
                                otherType,
                                wrapper.getName(),
                                null));
            } catch (BeanNoSuchPropertyException err) {
                // Acceptable, might have been tagged as @BeanProperty as well
            }
        }
        return wrapper;
    }

}
