package io.beanmapper.exceptions;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BeanNoNeighboursExceptionTest {

    @Test
    public void throwException() {
        try {
            throw new BeanNoNeighboursException();
        } catch (BeanNoNeighboursException ex) {
            assertTrue(ex.getMessage().contains("neighbouring classes"));
        }
    }

}