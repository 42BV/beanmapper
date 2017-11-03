package io.beanmapper.exceptions;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BeanConstructExceptionTest {

    @Test
    public void throwException() {
        try {
            throw new BeanConstructException(String.class, null);
        } catch (BeanConstructException ex) {
            assertTrue(ex.getMessage().contains("construct class java.lang.String"));
        }
    }
}
