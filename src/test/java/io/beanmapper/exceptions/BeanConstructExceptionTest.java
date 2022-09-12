package io.beanmapper.exceptions;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BeanConstructExceptionTest {

    @Test
    void throwException() {
        try {
            throw new BeanConstructException(String.class, null);
        } catch (BeanConstructException ex) {
            assertTrue(ex.getMessage().contains("construct class java.lang.String"));
        }
    }
}
