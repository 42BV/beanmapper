package io.beanmapper.config;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import io.beanmapper.BeanMapper;
import io.beanmapper.core.constructor.DefaultBeanInitializer;
import io.beanmapper.strategy.ConstructorArguments;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OverrideConfigurationTest {

    private CoreConfiguration coreConfiguration;

    private OverrideConfiguration overrideConfiguration;

    @BeforeEach
    void setup() {
        BeanMapper beanMapper = new BeanMapperBuilder()
                .build();
        coreConfiguration = (CoreConfiguration) beanMapper.configuration();
        overrideConfiguration = new OverrideConfiguration(coreConfiguration);
    }

    @Test
    void noParentConfig() {
        assertThrows(ParentConfigurationPossiblyNullException.class, () -> new OverrideConfiguration(null));
    }

    @Test
    void unsupportedCalls() {
        assertDoesNotThrow(() -> {
            overrideConfiguration.withoutDefaultConverters();
            overrideConfiguration.addProxySkipClass(null);
            overrideConfiguration.addPackagePrefix((String) null);
            overrideConfiguration.addPackagePrefix((Class<?>) null);
            overrideConfiguration.addAfterClearFlusher(null);
            overrideConfiguration.setBeanUnproxy(null);
        });
    }

    @Test
    void mustFlush() {
        overrideConfiguration.setFlushEnabled(true);
        overrideConfiguration.setFlushAfterClear(true);
        assertEquals(true, overrideConfiguration.isFlushEnabled());
        assertEquals(true, overrideConfiguration.isFlushAfterClear());
        assertEquals(true, overrideConfiguration.mustFlush());
    }

    @Test
    void mustFlush_flushAfterClearIsFalse() {
        overrideConfiguration.setFlushEnabled(true);
        overrideConfiguration.setFlushAfterClear(false);
        assertEquals(false, overrideConfiguration.mustFlush());
    }

    @Test
    void strictTargetSuffix() {
        assertEquals("Result", overrideConfiguration.getStrictTargetSuffix());
        overrideConfiguration.setStrictTargetSuffix("Result2");
        assertEquals("Result2", overrideConfiguration.getStrictTargetSuffix());
    }

    @Test
    void strictSourceSuffix() {
        assertEquals("Form", overrideConfiguration.getStrictSourceSuffix());
        overrideConfiguration.setStrictSourceSuffix("Form2");
        assertEquals("Form2", overrideConfiguration.getStrictSourceSuffix());
    }

    @Test
    void getCollectionHandlers() {
        assertEquals(coreConfiguration.getCollectionHandlers(), overrideConfiguration.getCollectionHandlers());
    }

    @Test
    void addBeanPairWithStrictSource() {
        overrideConfiguration.addBeanPairWithStrictSource(Long.class, String.class);
        List<BeanPair> beanPairs = overrideConfiguration.getBeanPairs();
        assertEquals(1, beanPairs.size());
        assertTrue(beanPairs.get(0).isSourceStrict());
        assertFalse(beanPairs.get(0).isTargetStrict());
    }

    @Test
    void addBeanPairWithStrictTarget() {
        overrideConfiguration.addBeanPairWithStrictTarget(Long.class, String.class);
        List<BeanPair> beanPairs = overrideConfiguration.getBeanPairs();
        assertEquals(1, beanPairs.size());
        assertFalse(beanPairs.get(0).isSourceStrict());
        assertTrue(beanPairs.get(0).isTargetStrict());
    }

    @Test
    void setBeanInitializer() {
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
    void determineTargetClass() {
        overrideConfiguration.setTarget("Hello");
        assertEquals(String.class, overrideConfiguration.determineTargetClass());
    }

}
