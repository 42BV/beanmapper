package io.beanmapper.exceptions;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BeanNoTargetExceptionTest {

    @Test
    void throwException() {
        try {
            throw new BeanNoTargetException();
        } catch (BeanNoTargetException ex) {
            assertTrue(ex.getMessage().contains("Unable to map"));
        }
    }
}
