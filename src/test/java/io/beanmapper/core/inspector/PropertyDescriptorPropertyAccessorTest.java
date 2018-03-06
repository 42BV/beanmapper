package io.beanmapper.core.inspector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.beans.PropertyDescriptor;

import io.beanmapper.exceptions.BeanGetFieldException;
import io.beanmapper.exceptions.BeanSetFieldException;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class PropertyDescriptorPropertyAccessorTest {

    @Test
    public void getValueShouldThrowBeanGetFieldExceptionWhenFieldIsNotReadable(@Mocked PropertyDescriptor mockDescriptor) {
        PropertyDescriptorPropertyAccessor accessor = new PropertyDescriptorPropertyAccessor(mockDescriptor);

        new Expectations() {{
            mockDescriptor.getReadMethod();
            result = null;

            mockDescriptor.getName();
            result = "bla";
        }};

        try {
            accessor.getValue("Instance");
            fail();
        } catch (BeanGetFieldException e) {
            assertEquals("Not possible to get field java.lang.String.bla", e.getMessage());
        }
    }

    @Test
    public void setValueShouldThrowBeanGetFieldExceptionWhenFieldIsNotReadable(@Mocked PropertyDescriptor mockDescriptor) {
        PropertyDescriptorPropertyAccessor accessor = new PropertyDescriptorPropertyAccessor(mockDescriptor);

        new Expectations() {{
            mockDescriptor.getWriteMethod();
            result = null;

            mockDescriptor.getName();
            result = "bla";
        }};

        try {
            accessor.setValue("Instance", "value");
            fail();
        } catch (BeanSetFieldException e) {
            assertEquals("Not possible to set field java.lang.String.bla", e.getMessage());
        }
    }
}