package io.beanmapper.exceptions;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BeanFieldNoMatchExceptionTest {

    @Test
    public void throwException() {
        try {
            throw new BeanFieldNoMatchException(String.class, "name");
        } catch (BeanFieldNoMatchException ex) {
            assertTrue(ex.getMessage().contains("String.name"));
        }
    }
}
