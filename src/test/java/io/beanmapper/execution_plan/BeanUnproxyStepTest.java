package io.beanmapper.execution_plan;

import io.beanmapper.core.unproxy.BeanUnproxy;
import io.beanmapper.core.unproxy.DefaultBeanUnproxy;
import io.beanmapper.core.unproxy.SkippingBeanUnproxy;

import org.junit.jupiter.api.BeforeAll;

class BeanUnproxyStepTest {

    private static BeanUnproxy unproxy;

    @BeforeAll
    static void setUp() {
        unproxy = new SkippingBeanUnproxy(new DefaultBeanUnproxy());
    }
}
