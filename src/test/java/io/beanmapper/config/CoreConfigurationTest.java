package io.beanmapper.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.beanmapper.exceptions.BeanConfigurationOperationNotAllowedException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CoreConfigurationTest {

    private CoreConfiguration configuration;

    @BeforeEach
    void setUp() {
        this.configuration = new CoreConfiguration();
    }

    @Test
    void setParent() {
        assertThrows(BeanConfigurationOperationNotAllowedException.class, () -> configuration.setParent(null));
    }

    @Test
    void setTarget() {
        assertThrows(BeanConfigurationOperationNotAllowedException.class, () -> configuration.setTarget("Hello world"));
    }

    @Test
    void determineTargetClass_noTargetSet() {
        assertNull(configuration.determineTargetClass());
    }

    @Test
    void addNullPackagePrefix() {
        configuration.addPackagePrefix((Class<?>) null);
        assertEquals(0, configuration.getPackagePrefixes().size());
    }

    @Test
    void addWithoutDefaultConverters() {
        configuration.withoutDefaultConverters();
        assertFalse(configuration.isAddDefaultConverters());
    }

    @Test
    void singleMapRunProperties() {
        assertNull(configuration.getDownsizeSource());
        assertNull(configuration.getDownsizeTarget());
        assertNull(configuration.getTargetClass());
        assertNull(configuration.getTarget());
        assertNull(configuration.getParent());
        assertNull(configuration.getCollectionClass());
        assertNull(configuration.getPreferredCollectionClass());
        assertNull(configuration.getCollectionHandlerForCollectionClass());
        assertFalse(configuration.isFlushAfterClear());
        assertFalse(configuration.mustFlush());
    }

}
