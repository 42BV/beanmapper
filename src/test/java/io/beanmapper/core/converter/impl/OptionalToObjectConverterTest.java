package io.beanmapper.core.converter.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Objects;
import java.util.Optional;

import io.beanmapper.BeanMapper;
import io.beanmapper.config.BeanMapperBuilder;
import io.beanmapper.testmodel.optional_getter.MyEntity;
import io.beanmapper.testmodel.optional_getter.MyEntityResult;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OptionalToObjectConverterTest {

    private OptionalToObjectConverter converter;

    private BeanMapper beanMapper;

    private MyEntity myEntity;

    @BeforeEach
    void setUp() {

        converter = new OptionalToObjectConverter();

        beanMapper = new BeanMapperBuilder().build();

        myEntity = new MyEntity();
        myEntity.value = "Henk";

        MyEntity child = new MyEntity();
        child.value = "Piet";

        myEntity.child = child;
    }

    @Test
    void convertEntityWithChild() {
        var result = this.beanMapper.map(myEntity, MyEntityResult.class);
        assertEquals(this.myEntity.value, result.value);
        assertEquals(this.myEntity.child.value, result.child.value);
        assertNull(result.child.child);
    }

    @Test
    void convertEntityWithoutChild() {
        this.myEntity.child = null;
        var result = this.beanMapper.map(myEntity, MyEntityResult.class);
        assertEquals(this.myEntity.value, result.value);
        assertNull(result.child);
    }

    @Test
    void matchIsTrueForSourceOptionalAndObjectTarget() {
        assertTrue(this.converter.match(Optional.class, Object.class));
    }

    @Test
    void matchIsFalseForSourceNotOptional() {
        assertFalse(this.converter.match(Object.class, Objects.class));
    }

    @Test
    void convertEntityWithChildToSameEntity() {
        var result = this.beanMapper.map(myEntity, MyEntity.class);
        assertEquals(myEntity.value, result.value);
        assertEquals(myEntity.child.value, result.child.value);
        assertEquals(myEntity.child.child, result.child.child);
    }

    @Test
    void convertEntityWithoutChildToSameEntity() {
        this.myEntity.child = null;
        var result = this.beanMapper.map(myEntity, MyEntity.class);
        assertEquals(myEntity.value, result.value);
        assertNull(result.child);
    }


}