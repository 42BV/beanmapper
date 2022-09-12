package io.beanmapper.exceptions;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BeanDynamicClassGenerationExceptionTest {

    @Test
    void throwException() {
        try {
            throw new BeanDynamicClassGenerationException(null, String.class, "name");
        } catch (BeanDynamicClassGenerationException ex) {
            assertTrue(ex.getMessage().contains("String with key name"));
        }
    }
}
