package io.beanmapper.config;

import org.junit.Test;

public class OverrideConfigurationTest {

    @Test(expected = ParentConfigurationPossiblyNullException.class)
    public void noParentConfig() {
        new OverrideConfiguration(null);
    }

}
