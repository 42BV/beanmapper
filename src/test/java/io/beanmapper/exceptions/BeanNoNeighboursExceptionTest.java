package io.beanmapper.exceptions;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BeanNoNeighboursExceptionTest {

    @Test
    void throwException() {
        try {
            throw new BeanNoNeighboursException();
        } catch (BeanNoNeighboursException ex) {
            assertTrue(ex.getMessage().contains("neighbouring classes"));
        }
    }

}