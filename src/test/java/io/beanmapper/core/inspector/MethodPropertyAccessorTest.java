package io.beanmapper.core.inspector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.beans.PropertyDescriptor;

import io.beanmapper.exceptions.BeanPropertyReadException;
import io.beanmapper.exceptions.BeanPropertyWriteException;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class MethodPropertyAccessorTest {

    @Test
    public void getValueShouldThrowBeanGetFieldExceptionWhenFieldIsNotReadable(@Mocked PropertyDescriptor mockDescriptor) {
        MethodPropertyAccessor accessor = new MethodPropertyAccessor(mockDescriptor);

        new Expectations() {{
            mockDescriptor.getReadMethod();
            result = null;

            mockDescriptor.getName();
            result = "bla";
        }};

        try {
            accessor.getValue("Instance");
            fail();
        } catch (BeanPropertyReadException e) {
            assertEquals("Not possible to get property java.lang.String.bla", e.getMessage());
        }
    }

    @Test
    public void setValueShouldThrowBeanGetFieldExceptionWhenFieldIsNotReadable(@Mocked PropertyDescriptor mockDescriptor) {
        MethodPropertyAccessor accessor = new MethodPropertyAccessor(mockDescriptor);

        new Expectations() {{
            mockDescriptor.getWriteMethod();
            result = null;

            mockDescriptor.getName();
            result = "bla";
        }};

        try {
            accessor.setValue("Instance", "value");
            fail();
        } catch (BeanPropertyWriteException e) {
            assertEquals("Not possible to set property java.lang.String.bla", e.getMessage());
        }
    }
}