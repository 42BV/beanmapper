package io.beanmapper.exceptions;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BeanDynamicClassGenerationExceptionTest {

    @Test
    public void throwException() {
        try {
            throw new BeanDynamicClassGenerationException(null, String.class, "name");
        } catch (BeanDynamicClassGenerationException ex) {
            assertTrue(ex.getMessage().contains("String with key name"));
        }
    }
}
