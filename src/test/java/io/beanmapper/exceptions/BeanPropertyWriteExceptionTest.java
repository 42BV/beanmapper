package io.beanmapper.exceptions;

import static org.junit.Assert.assertTrue;

import io.beanmapper.testmodel.defaults.SourceWithDefaults;

import org.junit.Test;

public class BeanPropertyWriteExceptionTest {

    @Test
    public void throwException() throws NoSuchFieldException {
        try {
            throw new BeanPropertyWriteException(SourceWithDefaults.class, "bothDefault");
        } catch (BeanMappingException e) {
            assertTrue("Must contain specific class and field name",
                    e.getMessage().contains("io.beanmapper.testmodel.defaults.SourceWithDefaults.bothDefault"));
        }
    }

}
