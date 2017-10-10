package io.beanmapper.exceptions;

import static org.junit.Assert.assertTrue;

import io.beanmapper.testmodel.defaults.SourceWithDefaults;

import org.junit.Test;

public class BeanInstantiationExceptionTest {

    @Test
    public void throwException() throws NoSuchFieldException {
        try {
            throw new BeanInstantiationException(SourceWithDefaults.class, null);
        } catch (BeanMappingException e) {
            assertTrue("Must contain specific class name",
                    e.getMessage().contains("io.beanmapper.testmodel.defaults.SourceWithDefaults"));
        }
    }

}
