package io.beanmapper.exceptions;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.beanmapper.testmodel.defaults.SourceWithDefaults;

import org.junit.jupiter.api.Test;

class BeanPropertyWriteExceptionTest {

    @Test
    void throwException() throws NoSuchFieldException {
        try {
            throw new BeanPropertyWriteException(SourceWithDefaults.class, "bothDefault");
        } catch (BeanMappingException e) {
            assertTrue(e.getMessage().contains("io.beanmapper.testmodel.defaults.SourceWithDefaults.bothDefault"),
                    "Must contain specific class and field name");
        }
    }

}
