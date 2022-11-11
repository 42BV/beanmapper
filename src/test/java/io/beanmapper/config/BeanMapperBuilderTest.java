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
import io.beanmapper.core.constructor.ConstructorArguments;
import io.beanmapper.core.constructor.DefaultBeanInitializer;
import io.beanmapper.core.unproxy.BeanUnproxy;
import io.beanmapper.exceptions.BeanConfigurationOperationNotAllowedException;
import io.beanmapper.utils.Trinary;

import org.junit.jupiter.api.Test;

class BeanMapperBuilderTest {

    @Test
    void setTargetClassOnCoreThrowsException() {
        assertThrows(BeanConfigurationOperationNotAllowedException.class, () -> {
            BeanMapperBuilder builder = new BeanMapperBuilder();
            builder.setTargetClass(null);
        });
    }

    @Test
    void setCollectionUsage() {
        assertThrows(BeanConfigurationOperationNotAllowedException.class, () -> {
            BeanMapperBuilder builder = new BeanMapperBuilder();
            builder.setCollectionUsage(null);
        });
    }

    @Test
    void setPreferredCollectionClass() {
        assertThrows(BeanConfigurationOperationNotAllowedException.class, () -> {
            BeanMapperBuilder builder = new BeanMapperBuilder();
            builder.setPreferredCollectionClass(null);
        });
    }

    @Test
    void setFlushAfterClear() {
        assertThrows(BeanConfigurationOperationNotAllowedException.class, () -> {
            BeanMapperBuilder builder = new BeanMapperBuilder();
            builder.setFlushAfterClear(Trinary.ENABLED);
        });
    }

    @Test
    void setTargetOnCoreThrowsException() {
        assertThrows(BeanConfigurationOperationNotAllowedException.class, () -> {
            BeanMapperBuilder builder = new BeanMapperBuilder();
            builder.setTarget(null);
        });
    }

    @Test
    void setDownsizeSourceOnCoreThrowsException() {
        assertThrows(BeanConfigurationOperationNotAllowedException.class, () -> {
            BeanMapperBuilder builder = new BeanMapperBuilder();
            builder.downsizeSource(null);
        });
    }

    @Test
    void setDownsizeTargetOnCoreThrowsException() {
        assertThrows(BeanConfigurationOperationNotAllowedException.class, () -> {
            BeanMapperBuilder builder = new BeanMapperBuilder();
            builder.downsizeTarget(null);
        });
    }

    @Test
    void setCollectionClassOnCoreThrowsException() {
        assertThrows(BeanConfigurationOperationNotAllowedException.class, () -> {
            BeanMapperBuilder builder = new BeanMapperBuilder();
            builder.setCollectionClass(null);
        });
    }

    @Test
    void withoutDefaultConverters() {
        BeanMapper beanMapper = new BeanMapperBuilder()
                .withoutDefaultConverters()
                .build();
        assertEquals(0, beanMapper.getConfiguration().getBeanConverters().size());
    }

    @Test
    void addCollectionHandler() {
        BeanMapper beanMapper = new BeanMapperBuilder()
                .addCollectionHandler(new ListCollectionHandler())
                .build();
        assertEquals(5, beanMapper.getConfiguration().getCollectionHandlers().size());
    }

    @Test
    void setProxySkipClass() {
        Class expectedClass = Collections.EMPTY_LIST.getClass();
        BeanMapper beanMapper = new BeanMapperBuilder()
                .addProxySkipClass(expectedClass)
                .build();
        assertEquals(expectedClass, beanMapper.getConfiguration().getBeanUnproxy().unproxy(expectedClass));
    }

    @Test
    void addPackagePrefix() {
        String expectedPackagePrefix = "io.beanmapper.dummie";
        BeanMapper beanMapper = new BeanMapperBuilder()
                .addPackagePrefix(expectedPackagePrefix)
                .build();
        List<String> packagePrefixes = beanMapper.getConfiguration().getPackagePrefixes();
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
        BeanMapper beanMapper = new BeanMapperBuilder()
                .setBeanInitializer(expectedBeanInitializer)
                .build();
        assertEquals(expectedBeanInitializer, beanMapper.getConfiguration().getBeanInitializer());
    }

    @Test
    void setBeanUnproxy() {
        final BeanUnproxy expectedBeanUnproxy = new BeanUnproxy() {
            @Override
            public Class<?> unproxy(Class<?> beanClass) {
                return Long.class;
            }
        };
        BeanMapper beanMapper = new BeanMapperBuilder()
                .setBeanUnproxy(expectedBeanUnproxy)
                .build();
        assertEquals(Long.class, beanMapper.getConfiguration().getBeanUnproxy().unproxy(String.class));
    }

    @Test
    void isConverterChoosableForCoreConfig() {
        BeanMapper beanMapper = new BeanMapperBuilder().build();
        assertFalse(beanMapper.getConfiguration().isConverterChoosable(),
                "The Core configuration assumes that a top-level call always assumes a pure mapping (ie, not using converters)"
        );
    }

    @Test
    void isConverterChoosableSettableForCoreConfig() {
        BeanMapper beanMapper = new BeanMapperBuilder()
                .setConverterChoosable(true)
                .build();
        assertTrue(beanMapper.getConfiguration().isConverterChoosable(),
                "The Core configuration converterChoosable is settable)"
        );
    }

    @Test
    void isConverterChoosableCustomForOverrideConfig() {
        BeanMapper beanMapper = new BeanMapperBuilder().build(); // Core wrap
        beanMapper = beanMapper
                .wrap()
                .setConverterChoosable(false) // <<< override the converter choosable option here
                .build(); // Override wrap
        assertFalse(beanMapper.getConfiguration().isConverterChoosable(),
                "The Override configuration has a custom override for the converter choosable option, which should be false");
    }

    @Test
    void newConfigOnOverrideLeadsToExistingConfig() {
        BeanMapper beanMapper = new BeanMapperBuilder().build(); // Core wrap
        beanMapper = beanMapper.wrap().build(); // Wrap in an override wrap
        Configuration currentConfiguration = beanMapper.getConfiguration();
        beanMapper = beanMapper.wrap().build(); // <<< explicitly wrap in an override wrap
        assertNotSame(
                currentConfiguration, beanMapper.getConfiguration(), "The configuration must be a new one from the one we already had");
    }

    @Test
    void strictMappingConventionForCoreConfig() {
        BeanMapper beanMapper = new BeanMapperBuilder()
                .setStrictSourceSuffix("Frm")
                .setStrictTargetSuffix("Rslt")
                .build(); // Core wrap
        Configuration currentConfiguration = beanMapper.getConfiguration();
        assertEquals("Frm", currentConfiguration.getStrictSourceSuffix());
        assertEquals("Rslt", currentConfiguration.getStrictTargetSuffix());
    }

    @Test
    void strictMappingConventionForOverrideConfig() {
        BeanMapper beanMapper = new BeanMapperBuilder().build(); // Core wrap
        beanMapper = beanMapper.wrap()
                .setStrictSourceSuffix("Frm")
                .build(); // Wrap in an override wrap

        Configuration currentConfiguration = beanMapper.getConfiguration();
        assertEquals("Frm",
                currentConfiguration.getStrictMappingProperties().getStrictSourceSuffix());
        assertEquals("Result",
                currentConfiguration.getStrictMappingProperties().getStrictTargetSuffix());
        assertTrue(currentConfiguration.getStrictMappingProperties().isApplyStrictMappingConvention());
    }

    @Test
    void cleanConfig() {
        BeanMapper beanMapper = new BeanMapperBuilder().build(); // Core wrap
        beanMapper = wrapAndSetFieldsForSingleRun(beanMapper);
        beanMapper = wrapAndSetFieldsForSingleRun(beanMapper); // Wrap it twice to make sure we have a parent
        // override wrap with the fields all set
        assertNotNull(beanMapper.getConfiguration().getCollectionClass(), "Container class must be set");
        assertNotNull(beanMapper.getConfiguration().getTargetClass(), "Target class must be set");
        assertNotNull(beanMapper.getConfiguration().getTarget(), "Target must be set");

        beanMapper = beanMapper.wrap().build();
        assertNull(beanMapper.getConfiguration().getCollectionClass(), "Container class must be cleared");
        assertNull(beanMapper.getConfiguration().getTargetClass(), "Target class must be cleared");
        assertNull(beanMapper.getConfiguration().getTarget(), "Target must be cleared");
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
