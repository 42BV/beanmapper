package io.beanmapper.exceptions;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BeanMissingPathExceptionTest {

    @Test
    public void throwException() {
        try {
            throw new BeanMissingPathException(String.class, "name", null);
        } catch (BeanMissingPathException ex) {
            assertTrue(ex.getMessage().contains("String.name"));
        }
    }
}
