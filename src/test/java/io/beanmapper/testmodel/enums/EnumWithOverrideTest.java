package io.beanmapper.testmodel.enums;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.Arrays;

import io.beanmapper.BeanMapper;
import io.beanmapper.config.BeanMapperBuilder;

import org.junit.jupiter.api.Test;

class EnumWithOverrideTest {

    @Test
    void mapToEnumWithOverrides() {
        BeanMapper mapper = new BeanMapperBuilder().setApplyStrictMappingConvention(false).build();
        assertDoesNotThrow(() -> Arrays.stream(EnumWithOverride.values())
                .map(v -> mapper.map(v, EnumWithOverrideResult.class))
                .toList()
        );
    }
}
