package io.beanmapper.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import io.beanmapper.exceptions.BeanConfigurationOperationNotAllowedException;

import org.junit.Test;

public class CoreConfigurationTest {

    @Test(expected = BeanConfigurationOperationNotAllowedException.class)
    public void setParent() {
        CoreConfiguration configuration = new CoreConfiguration();
        configuration.setParent(null);
    }

    @Test(expected = BeanConfigurationOperationNotAllowedException.class)
    public void setTarget() {
        CoreConfiguration configuration = new CoreConfiguration();
        configuration.setTarget("Hello world");
    }

    @Test
    public void determineTargetClass_noTargetSet() {
        CoreConfiguration configuration = new CoreConfiguration();
        assertNull(configuration.determineTargetClass());
    }

    @Test
    public void addNullPackagePrefix() {
        CoreConfiguration configuration = new CoreConfiguration();
        configuration.addPackagePrefix((Class<?>)null);
        assertEquals(0, configuration.getPackagePrefixes().size());
    }

    @Test
    public void addWithoutDefaultConverters() {
        CoreConfiguration configuration = new CoreConfiguration();
        configuration.withoutDefaultConverters();
        assertFalse(configuration.isAddDefaultConverters());
    }

    @Test
    public void singleMapRunProperties() {
        CoreConfiguration configuration = new CoreConfiguration();
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
