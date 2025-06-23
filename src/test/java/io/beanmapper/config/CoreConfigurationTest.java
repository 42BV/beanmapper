package io.beanmapper.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.beanmapper.BeanMapper;
import io.beanmapper.core.converter.impl.StringToLongConverter;
import io.beanmapper.exceptions.BeanConfigurationOperationNotAllowedException;
import io.beanmapper.utils.Trinary;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CoreConfigurationTest {

    private CoreConfiguration configuration;

    @BeforeEach
    void setUp() {
        configuration = (CoreConfiguration) BeanMapper.builder().build().configuration();
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

    @Test
    void testSetFlushEnabledShouldSucceed() {
        configuration.setFlushEnabled(true);
        assertTrue(configuration.isFlushEnabled());

        configuration.setFlushEnabled(false);
        assertFalse(configuration.isFlushEnabled());
    }

    @Test
    void testGetDefaultValueForUnknownTypeShouldReturnNull() {
        assertNull(configuration.getDefaultValueForClass(Object.class));
    }

    @Test
    void testGetDefaultValueForKnownTypeShouldReturnValue() {
        assertNotNull(configuration.getDefaultValueForClass(int.class));
    }

    @Test
    void testGetDefaultValueForCustomDefaultValueShouldReturnValue() {
        configuration.addCustomDefaultValueForClass(Object.class, new Object());
        assertNotNull(configuration.getDefaultValueForClass(Object.class));
    }

    @Test
    void testGetBeanConverterShouldReturnBeanConverter() {
        configuration.getBeanConverterStore().add(String.class, Long.class, new StringToLongConverter());
        assertNotNull(configuration.getBeanConverter(String.class, Long.class));
    }
}
