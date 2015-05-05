package io.beanmapper.exceptions;

import io.beanmapper.testmodel.defaults.SourceWithDefaults;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

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
