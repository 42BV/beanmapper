package io.beanmapper.execution_plan;

import io.beanmapper.execution_plan.converters.BeanConverters;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NoOpStepTest {

    @Test
    void testNoOpStepShouldReturnTheSameObject() {
        String testString = "Hello, World!";
        BeanConversionStep<String, String> a = BeanConverters.targetSameTypeConverter();
        String result = a.apply(testString);
        Assertions.assertEquals(testString, result);
        Assertions.assertSame(testString, result);
    }

    @Test
    void testNoOpStepShouldReturnTheSameObjectWithNulls() {
        String testString = null;
        BeanConversionStep<String, String> a = BeanConverters.targetSameTypeConverter();
        String result = a.apply(testString);
        Assertions.assertEquals(testString, result);
        Assertions.assertSame(testString, result);
    }
}
