package io.beanmapper.execution_plan;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import io.beanmapper.core.unproxy.BeanUnproxy;
import io.beanmapper.core.unproxy.DefaultBeanUnproxy;
import io.beanmapper.core.unproxy.SkippingBeanUnproxy;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class BeanUnproxyStepTest {

    private static BeanUnproxy unproxy;

    @BeforeAll
    static void setUp() {
        unproxy = new SkippingBeanUnproxy(new DefaultBeanUnproxy());
    }

    @Test
    void shouldReturnTheDeterminedUnproxiedClass() {
        ExecutionStep<String, String> lambda = (mapping) -> null;
        assertNotEquals(Runnable.class, lambda.getClass());

        Class<?> unproxiedClass = unproxy.unproxy(lambda.getClass());
        BeanUnproxyStep beanUnproxyStep = new BeanUnproxyStep(unproxiedClass);

        assertEquals(unproxiedClass, beanUnproxyStep.apply());
        assertEquals(Runnable.class, unproxiedClass);
    }
}
