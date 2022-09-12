package io.beanmapper.exceptions;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.beanmapper.testmodel.defaults.SourceWithDefaults;

import org.junit.jupiter.api.Test;

class BeanInstantiationExceptionTest {

    @Test
    void throwException() throws NoSuchFieldException {
        try {
            throw new BeanInstantiationException(SourceWithDefaults.class, null);
        } catch (BeanMappingException e) {
            assertTrue(e.getMessage().contains("io.beanmapper.testmodel.defaults.SourceWithDefaults"), "Must contain specific class name");
        }
    }

}
