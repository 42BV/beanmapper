package io.beanmapper.config;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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
import io.beanmapper.utils.Trinary;
import io.beanmapper.utils.diagnostics.DiagnosticsDetailLevel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class BeanMapperBuilderTest {

    private BeanMapperBuilder builder;

    @BeforeEach
    void setup() {
        builder = new BeanMapperBuilder();
    }

    @Test
    void setTargetClassOnCoreThrowsException() {
        assertThrows(BeanConfigurationOperationNotAllowedException.class, () -> {
            builder.setTargetClass(null);
        });
    }

    @Test
    void setCollectionUsage() {
        assertThrows(BeanConfigurationOperationNotAllowedException.class, () -> {
            builder.setCollectionUsage(null);
        });
    }

    @Test
    void setPreferredCollectionClass() {
        assertThrows(BeanConfigurationOperationNotAllowedException.class, () -> {
            builder.setPreferredCollectionClass(null);
        });
    }

    @Test
    void setFlushAfterClear() {
        assertThrows(BeanConfigurationOperationNotAllowedException.class, () -> {
            builder.setFlushAfterClear(Trinary.ENABLED);
        });
    }

    @Test
    void setTargetOnCoreThrowsException() {
        assertThrows(BeanConfigurationOperationNotAllowedException.class, () -> {
            builder.setTarget(null);
        });
    }

    @Test
    void setDownsizeSourceOnCoreThrowsException() {
        assertThrows(BeanConfigurationOperationNotAllowedException.class, () -> {
            builder.downsizeSource(null);
        });
    }

    @Test
    void setDownsizeTargetOnCoreThrowsException() {
        assertThrows(BeanConfigurationOperationNotAllowedException.class, () -> {
            builder.downsizeTarget(null);
        });
    }

    @Test
    void setCollectionClassOnCoreThrowsException() {
        assertThrows(BeanConfigurationOperationNotAllowedException.class, () -> {
            builder.setCollectionClass(null);
        });
    }

    @Test
    void withoutDefaultConverters() {
        BeanMapper beanMapper = builder
                .withoutDefaultConverters()
                .build();
        assertEquals(0, beanMapper.configuration().getBeanConverters().size());
    }

    @Test
    void addCollectionHandler() {
        BeanMapper beanMapper = builder
                .addCollectionHandler(new ListCollectionHandler())
                .build();
        assertEquals(5, beanMapper.configuration().getCollectionHandlers().size());
    }

    @Test
    void setProxySkipClass() {
        Class<?> expectedClass = Collections.emptyList().getClass();
        BeanMapper beanMapper = builder
                .addProxySkipClass(expectedClass)
                .build();
        assertEquals(expectedClass, beanMapper.configuration().getBeanUnproxy().unproxy(expectedClass));
    }

    @Test
    void addPackagePrefix() {
        String expectedPackagePrefix = "io.beanmapper.dummie";
        BeanMapper beanMapper = builder
                .addPackagePrefix(expectedPackagePrefix)
                .build();
        List<String> packagePrefixes = beanMapper.configuration().getPackagePrefixes();
        assertEquals(expectedPackagePrefix, packagePrefixes.getFirst());
    }

    @Test
    void setBeanInitializer() {
        final DefaultBeanInitializer expectedBeanInitializer = new DefaultBeanInitializer() {
            @Override
            public <T> T instantiate(Class<T> beanClass, ConstructorArguments arguments) {
                return null;
            }
        };
        BeanMapper beanMapper = builder
                .setBeanInitializer(expectedBeanInitializer)
                .build();
        assertEquals(expectedBeanInitializer, beanMapper.configuration().getBeanInitializer());
    }

    @Test
    @Disabled("BeanUnproxy should not be used as a Converter.")
    void setBeanUnproxy() {
        final BeanUnproxy expectedBeanUnproxy = beanClass -> Long.class;
        BeanMapper beanMapper = builder
                .setBeanUnproxy(expectedBeanUnproxy)
                .build();
        assertEquals(Long.class, beanMapper.configuration().getBeanUnproxy().unproxy(String.class));
    }

    @Test
    void isConverterChoosableForCoreConfig() {
        BeanMapper beanMapper = builder.build();
        assertFalse(beanMapper.configuration().isConverterChoosable(),
                "The Core configuration assumes that a top-level call always assumes a pure mapping (ie, not using converters)"
        );
    }

    @Test
    void isConverterChoosableSettableForCoreConfig() {
        BeanMapper beanMapper = builder
                .setConverterChoosable(true)
                .build();
        assertTrue(beanMapper.configuration().isConverterChoosable(),
                "The Core configuration converterChoosable is settable)"
        );
    }

    @Test
    void isConverterChoosableCustomForOverrideConfig() {
        BeanMapper beanMapper = builder.build(); // Core wrap
        beanMapper = beanMapper
                .wrap()
                .setConverterChoosable(false) // <<< override the converter choosable option here
                .build(); // Override wrap
        assertFalse(beanMapper.configuration().isConverterChoosable(),
                "The Override configuration has a custom override for the converter choosable option, which should be false");
    }

    @Test
    void newConfigOnOverrideLeadsToExistingConfig() {
        BeanMapper beanMapper = builder.build(); // Core wrap
        beanMapper = beanMapper.wrap().build(); // Wrap in an override wrap
        Configuration currentConfiguration = beanMapper.configuration();
        beanMapper = beanMapper.wrap().build(); // <<< explicitly wrap in an override wrap
        assertNotSame(
                currentConfiguration, beanMapper.configuration(), "The configuration must be a new one from the one we already had");
    }

    @Test
    void strictMappingConventionForCoreConfig() {
        BeanMapper beanMapper = builder
                .setStrictSourceSuffix("Frm")
                .setStrictTargetSuffix("Rslt")
                .build(); // Core wrap
        Configuration currentConfiguration = beanMapper.configuration();
        assertEquals("Frm", currentConfiguration.getStrictSourceSuffix());
        assertEquals("Rslt", currentConfiguration.getStrictTargetSuffix());
    }

    @Test
    void strictMappingConventionForOverrideConfig() {
        BeanMapper beanMapper = builder.build(); // Core wrap
        beanMapper = beanMapper.wrap()
                .setStrictSourceSuffix("Frm")
                .build(); // Wrap in an override wrap

        Configuration currentConfiguration = beanMapper.configuration();
        assertEquals("Frm",
                currentConfiguration.getStrictMappingProperties().getStrictSourceSuffix());
        assertEquals("Result",
                currentConfiguration.getStrictMappingProperties().getStrictTargetSuffix());
        assertTrue(currentConfiguration.getStrictMappingProperties().isApplyStrictMappingConvention());
    }

    @Test
    void createBeanMapperBuilderWithExistingConfiguration_shouldSucceed() {
        BeanMapper beanMapper = builder.build();
        Configuration currentConfiguration = beanMapper.configuration();
        assertDoesNotThrow(() -> new BeanMapperBuilder(currentConfiguration));
    }

    @Test
    void createBeanMapperBuilderWithExistingConfigurationAndDetailLevelDisabled_shouldSucceed() {
        Configuration currentConfiguration = builder.build().configuration();
        assertDoesNotThrow(() -> new BeanMapperBuilder(currentConfiguration, DiagnosticsDetailLevel.DISABLED));
    }

    @Test
    void createBeanMapperBuilderWithExistingDiagnosticsConfigAndDetailLevelDisabled_shouldSucceed() {
        Configuration diagnosticsConfiguration = builder.build().wrap(DiagnosticsDetailLevel.TREE_COMPLETE).build().configuration();
        assertDoesNotThrow(() -> new BeanMapperBuilder(diagnosticsConfiguration, DiagnosticsDetailLevel.DISABLED));
    }

    @Test
    void createBeanMapperBuilderWithExistingDiagnosticsConfigAndDetailLevelEnabled_shouldThrow() {
        Configuration diagnosticsConfiguration = builder.build().wrap(DiagnosticsDetailLevel.TREE_COMPLETE).build().configuration();
        assertThrows(BeanConfigurationOperationNotAllowedException.class, () -> new BeanMapperBuilder(diagnosticsConfiguration, DiagnosticsDetailLevel.TREE_COMPLETE));
    }

    @Test
    void createBeanMapperBuilderWithoutExistingDiagnosticsConfigAndDetailLevelEnabled_shouldThrow() {
        Configuration diagnosticsConfiguration = builder.build().configuration();
        assertDoesNotThrow(() -> new BeanMapperBuilder(diagnosticsConfiguration, DiagnosticsDetailLevel.TREE_COMPLETE));
    }

    @Test
    void setBeanUnproxy_shouldSucceed() {

        class BeanUnproxyImpl implements BeanUnproxy {
            @Override
            public Class<?> unproxy(Class<?> beanClass) {
                return null;
            }
        }

        BeanUnproxyImpl beanUnproxy = new BeanUnproxyImpl();

        assertDoesNotThrow(() -> builder = builder.setBeanUnproxy(beanUnproxy));
        BeanMapper beanMapper = builder.build();
        assertEquals(beanUnproxy, beanMapper.configuration().getBeanUnproxy().getDelegate());
    }

    @Test
    void cleanConfig() {
        BeanMapper beanMapper = builder.build(); // Core wrap
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
