package io.beanmapper.core.converter.collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import io.beanmapper.annotations.BeanCollectionUsage;
import io.beanmapper.core.BeanProperty;
import io.beanmapper.core.BeanPropertyCreator;
import io.beanmapper.core.BeanPropertyMatchupDirection;

import org.junit.jupiter.api.Test;

class BeanCollectionInstructionsTest {

    @Test
    void bothSidesHaveNoInstructions() {
        assertNull(BeanCollectionInstructions.merge(null, null));
    }

    @Test
    void onlyTargetSideGenericSupplied() {
        BeanProperty sourceBeanProperty = new BeanPropertyCreator(
                BeanPropertyMatchupDirection.SOURCE_TO_TARGET,
                SourceClassContainingList.class,
                "list")
                .determineNodesForPath();
        BeanProperty targetBeanProperty = new BeanPropertyCreator(
                BeanPropertyMatchupDirection.SOURCE_TO_TARGET,
                TargetClassContainingList.class,
                "list")
                .determineNodesForPath();

        targetBeanProperty.setCollectionInstructions(createBeanCollectionInstructions(
                CollectionElementType.derived(String.class),
                null,
                null,
                null));

        BeanCollectionInstructions merged = BeanCollectionInstructions.merge(sourceBeanProperty, targetBeanProperty);

        assertEquals(String.class, merged.getCollectionElementType().getType());
        assertEquals(BeanCollectionUsage.CLEAR, merged.getBeanCollectionUsage());
        assertTrue(merged.getPreferredCollectionClass().isEmpty());
        assertTrue(merged.getFlushAfterClear());
    }

    @Test
    void sourceSideSuppliesInstructionsAndTargetSideGeneric() {
        BeanProperty sourceBeanProperty = new BeanPropertyCreator(
                BeanPropertyMatchupDirection.SOURCE_TO_TARGET,
                SourceClassContainingList.class,
                "list")
                .determineNodesForPath();
        BeanProperty targetBeanProperty = new BeanPropertyCreator(
                BeanPropertyMatchupDirection.SOURCE_TO_TARGET,
                TargetClassContainingList.class,
                "list")
                .determineNodesForPath();

        sourceBeanProperty.setCollectionInstructions(createBeanCollectionInstructions(
                CollectionElementType.set(Long.class),
                BeanCollectionUsage.REUSE,
                new AnnotationClass(ArrayList.class),
                false));

        targetBeanProperty.setCollectionInstructions(createBeanCollectionInstructions(
                CollectionElementType.derived(String.class),
                null,
                null,
                null));

        BeanCollectionInstructions merged = BeanCollectionInstructions.merge(sourceBeanProperty, targetBeanProperty);

        assertEquals(Long.class, merged.getCollectionElementType().getType());
        assertEquals(BeanCollectionUsage.REUSE, merged.getBeanCollectionUsage());
        assertEquals(ArrayList.class, merged.getPreferredCollectionClass().getAnnotationClass());
        assertFalse(merged.getFlushAfterClear());
    }

    public class SourceClassContainingList {
        public List<String> list;
    }

    public class TargetClassContainingList {
        public List<String> list;
    }

    private BeanCollectionInstructions createBeanCollectionInstructions(
            CollectionElementType collectionElementType,
            BeanCollectionUsage beanCollectionUsage,
            AnnotationClass preferredCollectionClass,
            Boolean flushAfterClear) {
        BeanCollectionInstructions instructions = new BeanCollectionInstructions();
        instructions.setCollectionElementType(collectionElementType);
        instructions.setBeanCollectionUsage(beanCollectionUsage);
        instructions.setPreferredCollectionClass(preferredCollectionClass);
        instructions.setFlushAfterClear(flushAfterClear);
        return instructions;
    }

}
