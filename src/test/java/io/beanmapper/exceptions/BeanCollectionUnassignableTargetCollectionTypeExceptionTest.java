package io.beanmapper.exceptions;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BeanCollectionUnassignableTargetCollectionTypeExceptionTest {

    @Test
    void throwException() {
        try {
            throw new BeanCollectionUnassignableTargetCollectionTypeException(String.class, Long.class);
        } catch (BeanCollectionUnassignableTargetCollectionTypeException ex) {
            assertTrue(ex.getMessage().contains("String is not assignable from Long"));
        }
    }
}
