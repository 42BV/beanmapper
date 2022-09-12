package io.beanmapper.exceptions;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BeanCollectionNotSupportedExceptionTest {

    @Test
    void throwException() {
        try {
            throw new BeanCollectionNotSupportedException(String.class, Long.class);
        } catch (BeanCollectionNotSupportedException ex) {
            assertTrue(ex.getMessage().contains("String | Long"));
        }
    }
}
