package io.beanmapper.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;

import io.beanmapper.BeanMapper;
import io.beanmapper.core.collections.ListCollectionHandler;
import io.beanmapper.core.constructor.DefaultBeanInitializer;
import io.beanmapper.core.unproxy.BeanUnproxy;
import io.beanmapper.exceptions.BeanConfigurationOperationNotAllowedException;
import io.beanmapper.strategy.ConstructorArguments;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BeanMapperBuilderTest {

    private BeanMapperBuilder builder;

    @BeforeEach
    void setUp() {
        this.builder = new BeanMapperBuilder();
    }

    @Test
    void setTargetClassOnCoreThrowsException() {
        assertThrows(BeanConfigurationOperationNotAllowedException.class, () -> builder.setTargetClass(null));
    }

    @Test
    void setCollectionUsage() {
        assertThrows(BeanConfigurationOperationNotAllowedException.class, () -> builder.setCollectionUsage(null));
    }

    @Test
    void setPreferredCollectionClass() {
        assertThrows(BeanConfigurationOperationNotAllowedException.class, () -> builder.setPreferredCollectionClass(null));
    }

    @Test
    void setFlushAfterClear() {
        assertThrows(BeanConfigurationOperationNotAllowedException.class, () -> builder.setFlushAfterClear(true));
    }

    @Test
    void setTargetOnCoreThrowsException() {
        assertThrows(BeanConfigurationOperationNotAllowedException.class, () -> builder.setTarget(null));
    }

    @Test
    void setDownsizeSourceOnCoreThrowsException() {
        assertThrows(BeanConfigurationOperationNotAllowedException.class, () -> builder.downsizeSource(null));
    }

    @Test
    void setDownsizeTargetOnCoreThrowsException() {
        assertThrows(BeanConfigurationOperationNotAllowedException.class, () -> builder.downsizeTarget(null));
    }

    @Test
    void setCollectionClassOnCoreThrowsException() {
        assertThrows(BeanConfigurationOperationNotAllowedException.class, () -> builder.setCollectionClass(null));
    }

    @Test
    void withoutDefaultConverters() {
        BeanMapper beanMapper = this.builder
                .withoutDefaultConverters()
                .build();
        assertEquals(0, beanMapper.configuration().getBeanConverters().size());
    }

    @Test
    void addCollectionHandler() {
        BeanMapper beanMapper = this.builder
                .addCollectionHandler(new ListCollectionHandler())
                .build();
        assertEquals(4, beanMapper.configuration().getCollectionHandlers().size());
    }

    @Test
    void setProxySkipClass() {
        Class<?> expectedClass = Collections.EMPTY_LIST.getClass();
        BeanMapper beanMapper = this.builder
                .addProxySkipClass(expectedClass)
                .build();
        assertEquals(expectedClass, beanMapper.configuration().getBeanUnproxy().unproxy(expectedClass));
    }

    @Test
    void addPackagePrefix() {
        String expectedPackagePrefix = "io.beanmapper.dummy";
        BeanMapper beanMapper = this.builder
                .addPackagePrefix(expectedPackagePrefix)
                .build();
        List<String> packagePrefixes = beanMapper.configuration().getPackagePrefixes();
        assertEquals(expectedPackagePrefix, packagePrefixes.get(0));
    }

    @Test
    void setBeanInitializer() {
        final DefaultBeanInitializer expectedBeanInitializer = new DefaultBeanInitializer() {
            @Override
            public <T> T instantiate(Class<T> beanClass, ConstructorArguments arguments) {
                return null;
            }
        };
        BeanMapper beanMapper = this.builder
                .setBeanInitializer(expectedBeanInitializer)
                .build();
        assertEquals(expectedBeanInitializer, beanMapper.configuration().getBeanInitializer());
    }

    @Test
    void setBeanUnproxy() {
        final BeanUnproxy expectedBeanUnproxy = beanClass -> Long.class;
        BeanMapper beanMapper = this.builder
                .setBeanUnproxy(expectedBeanUnproxy)
                .build();
        assertEquals(Long.class, beanMapper.configuration().getBeanUnproxy().unproxy(String.class));
    }

    @Test
    void isConverterChoosableForCoreConfig() {
        BeanMapper beanMapper = this.builder.build();
        assertFalse(beanMapper.configuration().isConverterChoosable(),
                "The Core configuration assumes that a top-level call always assumes a pure mapping (ie, not using converters)"
        );
    }

    @Test
    void isConverterChoosableSettableForCoreConfig() {
        BeanMapper beanMapper = this.builder
                .setConverterChoosable(true)
                .build();
        assertTrue(beanMapper.configuration().isConverterChoosable(),
                "The Core configuration converterChoosable is settable)"
        );
    }

    @Test
    void isConverterChoosableCustomForOverrideConfig() {
        BeanMapper beanMapper = this.builder.build(); // Core wrap
        beanMapper = beanMapper
                .wrap()
                .setConverterChoosable(false) // <<< override the converter choosable option here
                .build(); // Override wrap
        assertFalse(beanMapper.configuration().isConverterChoosable(),
                "The Override configuration has a custom override for the converter choosable option, which should be false");
    }

    @Test
    void newConfigOnOverrideLeadsToExistingConfig() {
        BeanMapper beanMapper = this.builder.build(); // Core wrap
        beanMapper = beanMapper.wrap().build(); // Wrap in an override wrap
        Configuration currentConfiguration = beanMapper.configuration();
        beanMapper = beanMapper.wrap().build(); // <<< explicitly wrap in an override wrap
        assertNotSame(
                currentConfiguration, beanMapper.configuration(), "The configuration must be a new one from the one we already had");
    }

    @Test
    void strictMappingConventionForCoreConfig() {
        BeanMapper beanMapper = this.builder
                .setStrictSourceSuffix("From")
                .setStrictTargetSuffix("Result")
                .build(); // Core wrap
        Configuration currentConfiguration = beanMapper.configuration();
        assertEquals("From", currentConfiguration.getStrictSourceSuffix());
        assertEquals("Result", currentConfiguration.getStrictTargetSuffix());
    }

    @Test
    void strictMappingConventionForOverrideConfig() {
        BeanMapper beanMapper = this.builder.build(); // Core wrap
        beanMapper = beanMapper.wrap()
                .setStrictSourceSuffix("From")
                .build(); // Wrap in an override wrap

        Configuration currentConfiguration = beanMapper.configuration();
        assertEquals("From",
                currentConfiguration.getStrictMappingProperties().getStrictSourceSuffix());
        assertEquals("Result",
                currentConfiguration.getStrictMappingProperties().getStrictTargetSuffix());
        assertEquals(true,
                currentConfiguration.getStrictMappingProperties().isApplyStrictMappingConvention());
    }

    @Test
    void cleanConfig() {
        BeanMapper beanMapper = this.builder.build(); // Core wrap
        beanMapper = wrapAndSetFieldsForSingleRun(beanMapper);
        beanMapper = wrapAndSetFieldsForSingleRun(beanMapper); // Wrap it twice to make sure we have a parent
        // override wrap with the fields all set
        assertNotNull(beanMapper.configuration().getCollectionClass(), "Container class must be set");
        assertNotNull(beanMapper.configuration().getTargetClass(), "Target class must be set");
        assertNotNull(beanMapper.configuration().getTarget(), "Target must be set");

        beanMapper = beanMapper.wrap().build();
        assertNull(beanMapper.configuration().getCollectionClass(), "Container class must be cleared");
        assertNull(beanMapper.configuration().getTargetClass(), "Target class must be cleared");
        assertNull(beanMapper.configuration().getTarget(), "Target must be cleared");
    }

    private BeanMapper wrapAndSetFieldsForSingleRun(BeanMapper beanMapper) {
        return beanMapper
                .wrap()
                .setTargetClass(String.class)
                .setTarget("Hello world!")
                .setCollectionClass(String.class)
                .build(); // Wrap in an override wrap
    }

}
