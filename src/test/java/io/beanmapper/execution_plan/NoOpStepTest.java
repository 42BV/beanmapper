package io.beanmapper.execution_plan;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NoOpStepTest {

    @Test
    void testNoOpStepShouldReturnTheSameObject() {
        String testString = "Hello, World!";
        ExecutionStep<String, String, Void> a = new NoOpStep<>();
        String result = a.apply(testString);
        Assertions.assertEquals(testString, result);
        Assertions.assertSame(testString, result);
    }

    @Test
    void testNoOpStepShouldReturnTheSameObjectWithNulls() {
        String testString = null;
        ExecutionStep<String, String, Void> a = new NoOpStep<>();
        String result = a.apply(testString);
        Assertions.assertEquals(testString, result);
        Assertions.assertSame(testString, result);
    }
}
