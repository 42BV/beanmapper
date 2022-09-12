package io.beanmapper.exceptions;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BeanMissingPathExceptionTest {

    @Test
    void throwException() {
        try {
            throw new BeanMissingPathException(String.class, "name", null);
        } catch (BeanMissingPathException ex) {
            assertTrue(ex.getMessage().contains("String.name"));
        }
    }
}
