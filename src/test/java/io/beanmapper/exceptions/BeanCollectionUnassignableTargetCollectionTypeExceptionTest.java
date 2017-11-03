package io.beanmapper.exceptions;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BeanCollectionUnassignableTargetCollectionTypeExceptionTest {

    @Test
    public void throwException() {
        try {
            throw new BeanCollectionUnassignableTargetCollectionTypeException(String.class, Long.class);
        } catch (BeanCollectionUnassignableTargetCollectionTypeException ex) {
            assertTrue(ex.getMessage().contains("String is not assignable from Long"));
        }
    }
}
