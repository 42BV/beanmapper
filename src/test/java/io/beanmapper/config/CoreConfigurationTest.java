package io.beanmapper.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.beanmapper.exceptions.BeanConfigurationOperationNotAllowedException;
import io.beanmapper.utils.Trinary;

import org.junit.jupiter.api.Test;

class CoreConfigurationTest {

    @Test
    void setParent() {
        assertThrows(BeanConfigurationOperationNotAllowedException.class, () -> {
            CoreConfiguration configuration = new CoreConfiguration();
            configuration.setParent(null);
        });
    }

    @Test
    void setTarget() {
        assertThrows(BeanConfigurationOperationNotAllowedException.class, () -> {
            CoreConfiguration configuration = new CoreConfiguration();
            configuration.setTarget("Hello world");
        });
    }

    @Test
    void determineTargetClass_noTargetSet() {
        CoreConfiguration configuration = new CoreConfiguration();
        assertNull(configuration.determineTargetClass());
    }

    @Test
    void addNullPackagePrefix() {
        CoreConfiguration configuration = new CoreConfiguration();
        configuration.addPackagePrefix((Class<?>) null);
        assertEquals(0, configuration.getPackagePrefixes().size());
    }

    @Test
    void addWithoutDefaultConverters() {
        CoreConfiguration configuration = new CoreConfiguration();
        configuration.withoutDefaultConverters();
        assertFalse(configuration.isAddDefaultConverters());
    }

    @Test
    void singleMapRunProperties() {
        CoreConfiguration configuration = new CoreConfiguration();
        assertTrue(configuration.getDownsizeSource().isEmpty());
        assertTrue(configuration.getDownsizeTarget().isEmpty());
        assertNull(configuration.getTargetClass());
        assertNull(configuration.getTarget());
        assertNull(configuration.getParent());
        assertNull(configuration.getCollectionClass());
        assertNull(configuration.getPreferredCollectionClass());
        assertNull(configuration.getCollectionHandlerForCollectionClass());
        assertEquals(Trinary.DISABLED, configuration.isFlushAfterClear());
        assertFalse(configuration.mustFlush());
    }

}
