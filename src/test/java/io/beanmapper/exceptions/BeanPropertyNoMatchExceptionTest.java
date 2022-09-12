package io.beanmapper.exceptions;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BeanPropertyNoMatchExceptionTest {

    @Test
    void throwException() {
        try {
            throw new BeanPropertyNoMatchException(String.class, "name");
        } catch (BeanPropertyNoMatchException ex) {
            assertTrue(ex.getMessage().contains("String.name"));
        }
    }
}
