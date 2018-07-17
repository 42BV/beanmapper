package io.beanmapper.core.converter.collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import io.beanmapper.annotations.BeanCollectionUsage;
import io.beanmapper.core.BeanProperty;
import io.beanmapper.core.BeanPropertyCreator;
import io.beanmapper.core.BeanPropertyMatchupDirection;

import org.junit.Test;

public class BeanCollectionInstructionsTest {

    @Test
    public void bothSidesHaveNoInstructions() {
        assertNull(BeanCollectionInstructions.merge(null, null));
    }

    @Test
    public void onlyTargetSideGenericSupplied() {
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
    public void sourceSideSuppliesInstructionsAndTargetSideGeneric() {
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
