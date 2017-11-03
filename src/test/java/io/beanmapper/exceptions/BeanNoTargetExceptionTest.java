package io.beanmapper.exceptions;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BeanNoTargetExceptionTest {

    @Test
    public void throwException() {
        try {
            throw new BeanNoTargetException();
        } catch (BeanNoTargetException ex) {
            assertTrue(ex.getMessage().contains("Unable to map"));
        }
    }
}
