package io.beanmapper.exceptions;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BeanPropertyNoMatchExceptionTest {

    @Test
    public void throwException() {
        try {
            throw new BeanPropertyNoMatchException(String.class, "name");
        } catch (BeanPropertyNoMatchException ex) {
            assertTrue(ex.getMessage().contains("String.name"));
        }
    }
}
