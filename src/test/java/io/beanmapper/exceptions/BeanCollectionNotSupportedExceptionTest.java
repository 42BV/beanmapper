package io.beanmapper.exceptions;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BeanCollectionNotSupportedExceptionTest {

    @Test
    public void throwException() {
        try {
            throw new BeanCollectionNotSupportedException(String.class, Long.class);
        } catch (BeanCollectionNotSupportedException ex) {
            assertTrue(ex.getMessage().contains("String | Long"));
        }
    }
}
