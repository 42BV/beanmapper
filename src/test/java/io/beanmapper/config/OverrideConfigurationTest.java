package io.beanmapper.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import io.beanmapper.BeanMapper;
import io.beanmapper.core.constructor.DefaultBeanInitializer;
import io.beanmapper.strategy.ConstructorArguments;

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
    public void mustFlush_flushAfterClearIsFalse() {
        overrideConfiguration.setFlushEnabled(true);
        overrideConfiguration.setFlushAfterClear(false);
        assertEquals(false, overrideConfiguration.mustFlush());
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

    @Test
    public void getCollectionHandlers() {
        assertEquals(coreConfiguration.getCollectionHandlers(), overrideConfiguration.getCollectionHandlers());
    }

    @Test
    public void addBeanPairWithStrictSource() {
        overrideConfiguration.addBeanPairWithStrictSource(Long.class, String.class);
        List<BeanPair> beanPairs = overrideConfiguration.getBeanPairs();
        assertEquals(1, beanPairs.size());
        assertTrue(beanPairs.get(0).isSourceStrict());
        assertFalse(beanPairs.get(0).isTargetStrict());
    }

    @Test
    public void addBeanPairWithStrictTarget() {
        overrideConfiguration.addBeanPairWithStrictTarget(Long.class, String.class);
        List<BeanPair> beanPairs = overrideConfiguration.getBeanPairs();
        assertEquals(1, beanPairs.size());
        assertFalse(beanPairs.get(0).isSourceStrict());
        assertTrue(beanPairs.get(0).isTargetStrict());
    }

    @Test
    public void setBeanInitializer() {
        final DefaultBeanInitializer expectedBeanInitializer = new DefaultBeanInitializer() {
            @Override
            public <T> T instantiate(Class<T> beanClass, ConstructorArguments arguments) {
                return null;
            }
        };
        overrideConfiguration.setBeanInitializer(expectedBeanInitializer);
        assertEquals(expectedBeanInitializer, overrideConfiguration.getBeanInitializer());
    }

    @Test
    public void determineTargetClass() {
        overrideConfiguration.setTarget("Hello");
        assertEquals(String.class, overrideConfiguration.determineTargetClass());
    }

}
