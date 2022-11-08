package io.beanmapper.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

import io.beanmapper.BeanMapper;
import io.beanmapper.core.constructor.DefaultBeanInitializer;
import io.beanmapper.exceptions.BeanConfigurationOperationNotAllowedException;
import io.beanmapper.strategy.ConstructorArguments;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OverrideConfigurationTest {

    private CoreConfiguration coreConfiguration;

    private OverrideConfiguration overrideConfiguration;

    @BeforeEach
    void setup() {
        BeanMapper beanMapper = new BeanMapperBuilder()
                .build();
        coreConfiguration = (CoreConfiguration) beanMapper.getConfiguration();
        overrideConfiguration = new OverrideConfiguration(coreConfiguration);
    }

    @Test
    void noParentConfig() {
        assertThrows(ParentConfigurationPossiblyNullException.class, () -> new OverrideConfiguration(null));
    }

    @Test
    void unsupportedCalls() {
        assertThrows(BeanConfigurationOperationNotAllowedException.class, () -> overrideConfiguration.withoutDefaultConverters());
        assertThrows(BeanConfigurationOperationNotAllowedException.class, () -> overrideConfiguration.addProxySkipClass(null));
        assertThrows(BeanConfigurationOperationNotAllowedException.class, () -> overrideConfiguration.addPackagePrefix((String) null));
        assertThrows(BeanConfigurationOperationNotAllowedException.class, () -> overrideConfiguration.addPackagePrefix((Class) null));
        assertThrows(BeanConfigurationOperationNotAllowedException.class, () -> overrideConfiguration.addAfterClearFlusher(null));
        assertThrows(BeanConfigurationOperationNotAllowedException.class, () -> overrideConfiguration.setBeanUnproxy(null));
        assertThrows(BeanConfigurationOperationNotAllowedException.class, () -> overrideConfiguration.setRoleSecuredCheck(null));
        assertThrows(BeanConfigurationOperationNotAllowedException.class, () -> overrideConfiguration.addCollectionHandler(null));
        assertThrows(BeanConfigurationOperationNotAllowedException.class, () -> overrideConfiguration.addLogicSecuredCheck(null));
    }

    @Test
    void mustFlush() {
        overrideConfiguration.setFlushEnabled(true);
        overrideConfiguration.setFlushAfterClear(Trinary.ENABLED);
        assertTrue(overrideConfiguration.isFlushEnabled());
        assertEquals(Trinary.ENABLED, overrideConfiguration.isFlushAfterClear());
        assertTrue(overrideConfiguration.mustFlush());
    }

    @Test
    void mustFlush_flushAfterClearIsFalse() {
        overrideConfiguration.setFlushEnabled(true);
        overrideConfiguration.setFlushAfterClear(Trinary.DISABLED);
        assertFalse(overrideConfiguration.mustFlush());
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

    @Test
    void testAddCustomDefaultValueForQueue() {
        overrideConfiguration.addCustomDefaultValueForClass(Queue.class, new ArrayDeque<>());
        assertEquals(ArrayDeque.class, this.overrideConfiguration.getDefaultValueForClass(Queue.class).getClass());
    }

    @Test
    void testSetEnforceSecuredProperties_true() {
        overrideConfiguration.setEnforceSecuredProperties(true);
        assertTrue(overrideConfiguration.getEnforceSecuredProperties());
    }

    @Test
    void testSetEnforceSecuredProperties_false() {
        overrideConfiguration.setEnforceSecuredProperties(false);
        assertFalse(overrideConfiguration.getEnforceSecuredProperties());
    }

}
