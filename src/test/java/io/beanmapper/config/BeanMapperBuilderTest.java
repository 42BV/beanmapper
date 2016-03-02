package io.beanmapper.config;

import io.beanmapper.BeanMapper;
import io.beanmapper.exceptions.BeanConfigurationOperationNotAllowedException;
import mockit.Deencapsulation;
import org.junit.Test;

import static org.junit.Assert.*;

public class BeanMapperBuilderTest {

    @Test(expected = BeanConfigurationOperationNotAllowedException.class)
    public void setTargetClassOnCoreThrowsException() {
        BeanMapperBuilder builder = new BeanMapperBuilder();
        builder.setTargetClass(null);
    }

    @Test(expected = BeanConfigurationOperationNotAllowedException.class)
    public void setTargetOnCoreThrowsException() {
        BeanMapperBuilder builder = new BeanMapperBuilder();
        builder.setTarget(null);
    }

    @Test(expected = BeanConfigurationOperationNotAllowedException.class)
    public void setMappableFieldsOnCoreThrowsException() {
        BeanMapperBuilder builder = new BeanMapperBuilder();
        builder.setMappableFields(null);
    }

    @Test(expected = BeanConfigurationOperationNotAllowedException.class)
    public void setConverterChoosableOnCoreThrowsException() {
        BeanMapperBuilder builder = new BeanMapperBuilder();
        builder.setConverterChoosable(false);
    }

    @Test(expected = BeanConfigurationOperationNotAllowedException.class)
    public void setCollectionClassOnCoreThrowsException() {
        BeanMapperBuilder builder = new BeanMapperBuilder();
        builder.setCollectionClass(null);
    }

    @Test
    public void isConverterChoosableForCoreConfig() {
        BeanMapper beanMapper = new BeanMapperBuilder().build();
        assertFalse(
                "The Core configuration assumes that a top-level call always assumes a pure mapping (ie, not using converters)",
                beanMapper.getConfiguration().isConverterChoosable());
    }

    @Test
    public void isConverterChoosableDefaultForOverrideConfig() {
        BeanMapper beanMapper = new BeanMapperBuilder().build(); // Core wrapConfig
        beanMapper = beanMapper
                .config()
                .build(); // Override wrapConfig
        assertTrue(
                "The Override configuration assumes by default that are likely to be chosen for lower calls",
                beanMapper.getConfiguration().isConverterChoosable());
    }

    @Test
    public void isConverterChoosableCustomForOverrideConfig() {
        BeanMapper beanMapper = new BeanMapperBuilder().build(); // Core wrapConfig
        beanMapper = beanMapper
                .config()
                .setConverterChoosable(false) // <<< override the converter choosable option here
                .build(); // Override wrapConfig
        assertFalse(
                "The Override configuration has a custom override for the converter choosable option, which should be false",
                beanMapper.getConfiguration().isConverterChoosable());
    }

    @Test
    public void currentConfigOnCoreLeadsToOverrideConfig() {
        BeanMapper beanMapper = new BeanMapperBuilder().build(); // Core wrapConfig
        beanMapper = beanMapper.config().build(); // Wrap in an override config
        Object parentConfiguration = Deencapsulation.getField(beanMapper.getConfiguration(), "parentConfiguration");
        assertNotNull(parentConfiguration);
    }

    @Test
    public void currentConfigOnOverrideLeadsToExistingConfig() {
        BeanMapper beanMapper = new BeanMapperBuilder().build(); // Core wrapConfig
        beanMapper = beanMapper.config().build(); // Wrap in an override config
        Configuration currentConfiguration = beanMapper.getConfiguration();
        beanMapper = beanMapper.config().build(); // <<< explicitly fetch the existing configu
        assertEquals("The configuration must be the same as the one we already had",
                currentConfiguration, beanMapper.getConfiguration());
    }

    @Test
    public void newConfigOnOverrideLeadsToExistingConfig() {
        BeanMapper beanMapper = new BeanMapperBuilder().build(); // Core wrapConfig
        beanMapper = beanMapper.config().build(); // Wrap in an override config
        Configuration currentConfiguration = beanMapper.getConfiguration();
        beanMapper = beanMapper.wrapConfig().build(); // <<< explicitly wrap in an override config
        assertNotSame("The configuration must be a new one from the one we already had",
                currentConfiguration, beanMapper.getConfiguration());
    }

    @Test
    public void cleanConfig() {
        BeanMapper beanMapper = new BeanMapperBuilder().build(); // Core wrapConfig
        beanMapper = wrapAndSetFieldsForSingleRun(beanMapper);
        beanMapper = wrapAndSetFieldsForSingleRun(beanMapper); // Wrap it twice to make sure we have a parent
                                                               // override config with the fields all set
        assertNotNull("Container class must be set", beanMapper.getConfiguration().getCollectionClass());
        assertNotNull("Target class must be set", beanMapper.getConfiguration().getTargetClass());
        assertNotNull("Target must be set", beanMapper.getConfiguration().getTarget());

        beanMapper = beanMapper.clear().build();
        assertNull("Container class must be cleared", beanMapper.getConfiguration().getCollectionClass());
        assertNull("Target class must be cleared", beanMapper.getConfiguration().getTargetClass());
        assertNull("Target must be cleared", beanMapper.getConfiguration().getTarget());
    }

    private BeanMapper wrapAndSetFieldsForSingleRun(BeanMapper beanMapper) {
        return beanMapper
                .wrapConfig()
                .setTargetClass(String.class)
                .setTarget("Hello world!")
                .setCollectionClass(String.class)
                .build(); // Wrap in an override config
    }

}
