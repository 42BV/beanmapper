package io.beanmapper.exceptions;

import io.beanmapper.testmodel.defaults.SourceWithDefaults;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class BeanSetFieldExceptionTest {

    @Test
    public void throwException() throws NoSuchFieldException {
        try {
            throw new BeanSetFieldException (SourceWithDefaults.class,
                    SourceWithDefaults.class.getDeclaredField("bothDefault"), null);
        } catch (BeanMappingException e) {
            assertTrue("Must contain specific class and field name",
                    e.getMessage().contains("io.beanmapper.testmodel.defaults.SourceWithDefaults.bothDefault"));
        }
    }

}
