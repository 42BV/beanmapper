package io.beanmapper.core.converter.collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import io.beanmapper.annotations.BeanCollectionUsage;
import io.beanmapper.core.BeanField;

import org.junit.Test;

public class BeanCollectionInstructionsTest {

    @Test
    public void bothSidesHaveNoInstructions() {
        assertNull(BeanCollectionInstructions.merge(null, null));
    }

    @Test
    public void onlyTargetSideGenericSupplied() {
        BeanField sourceBeanField = new BeanField("alpha", null);
        BeanField targetBeanField = new BeanField("alpha", null);

        targetBeanField.setCollectionInstructions(createBeanCollectionInstructions(
                CollectionElementType.derived(String.class),
                null,
                null,
                null));

        BeanCollectionInstructions merged = BeanCollectionInstructions.merge(sourceBeanField, targetBeanField);

        assertEquals(String.class, merged.getCollectionElementType().getType());
        assertEquals(BeanCollectionUsage.CLEAR, merged.getBeanCollectionUsage());
        assertTrue(merged.getPreferredCollectionClass().isEmpty());
        assertTrue(merged.getFlushAfterClear());
    }

    @Test
    public void sourceSideSuppliesInstructionsAndTargetSideGeneric() {
        BeanField sourceBeanField = new BeanField("alpha", null);
        BeanField targetBeanField = new BeanField("alpha", null);

        sourceBeanField.setCollectionInstructions(createBeanCollectionInstructions(
                CollectionElementType.set(Long.class),
                BeanCollectionUsage.REUSE,
                new AnnotationClass(ArrayList.class),
                false));

        targetBeanField.setCollectionInstructions(createBeanCollectionInstructions(
                CollectionElementType.derived(String.class),
                null,
                null,
                null));

        BeanCollectionInstructions merged = BeanCollectionInstructions.merge(sourceBeanField, targetBeanField);

        assertEquals(Long.class, merged.getCollectionElementType().getType());
        assertEquals(BeanCollectionUsage.REUSE, merged.getBeanCollectionUsage());
        assertEquals(ArrayList.class, merged.getPreferredCollectionClass().getAnnotationClass());
        assertFalse(merged.getFlushAfterClear());
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
