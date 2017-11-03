package io.beanmapper.config;

import static org.junit.Assert.assertEquals;

import io.beanmapper.BeanMapper;

import org.junit.Before;
import org.junit.Test;

public class OverrideConfigurationTest {

    private CoreConfiguration coreConfiguration;

    private OverrideConfiguration overrideConfiguration;

    @Before
    public void setup() {
        BeanMapper beanMapper = new BeanMapperBuilder()
                .build();
        coreConfiguration = (CoreConfiguration)beanMapper.getConfiguration();
        overrideConfiguration = new OverrideConfiguration(coreConfiguration);
    }

    @Test(expected = ParentConfigurationPossiblyNullException.class)
    public void noParentConfig() {
        new OverrideConfiguration(null);
    }

    @Test
    public void unsupportedCalls() {
        overrideConfiguration.withoutDefaultConverters();
        overrideConfiguration.addProxySkipClass(null);
        overrideConfiguration.addPackagePrefix((String)null);
        overrideConfiguration.addPackagePrefix((Class)null);
        overrideConfiguration.addAfterClearFlusher(null);
        overrideConfiguration.setBeanUnproxy(null);
    }

    @Test
    public void mustFlush() {
        overrideConfiguration.setFlushEnabled(true);
        overrideConfiguration.setFlushAfterClear(true);
        assertEquals(true, overrideConfiguration.isFlushEnabled());
        assertEquals(true, overrideConfiguration.isFlushAfterClear());
        assertEquals(true, overrideConfiguration.mustFlush());
    }

    @Test
    public void strictTargetSuffix() {
        assertEquals("Result", overrideConfiguration.getStrictTargetSuffix());
        overrideConfiguration.setStrictTargetSuffix("Result2");
        assertEquals("Result2", overrideConfiguration.getStrictTargetSuffix());
    }

    @Test
    public void strictSourceSuffix() {
        assertEquals("Form", overrideConfiguration.getStrictSourceSuffix());
        overrideConfiguration.setStrictSourceSuffix("Form2");
        assertEquals("Form2", overrideConfiguration.getStrictSourceSuffix());
    }

}
