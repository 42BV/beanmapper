package io.beanmapper.annotations;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.beanmapper.BeanMapper;
import io.beanmapper.annotations.model.bean_alias.DownsizeForm;
import io.beanmapper.annotations.model.bean_alias.Result;
import io.beanmapper.config.BeanMapperBuilder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BeanAliasTest {

    private BeanMapper beanMapper;

    @BeforeEach
    void setUp() {
        this.beanMapper = new BeanMapperBuilder().build();
    }

    @Test
    void testDownsizeSourceWithBeanAliasedField() {
        this.beanMapper = this.beanMapper.wrap()
                .setApplyStrictMappingConvention(false)
                .setTargetClass(Result.class)
                .build();

        var form = new DownsizeForm();
        form.name = "Henk";

        var result = assertDoesNotThrow(() -> this.beanMapper.map(form));

        assertEquals("Henk", assertDoesNotThrow(() -> result.getClass().getField("test").get(result)));

    }

}
