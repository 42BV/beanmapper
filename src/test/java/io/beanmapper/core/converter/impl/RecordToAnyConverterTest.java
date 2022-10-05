package io.beanmapper.core.converter.impl;

import static org.junit.jupiter.api.Assertions.*;

import io.beanmapper.config.BeanMapperBuilder;
import io.beanmapper.testmodel.record.PersonRecordWithNestedRecord;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RecordToAnyConverterTest {

    private RecordToAnyConverter converter;

    @BeforeEach
    public void setUp() {
        this.converter = new RecordToAnyConverter();
    }

    @Test
    void isMatchTrueIfSourceIsRecord() {
        assertTrue(converter.match(PersonRecordWithNestedRecord.class, Object.class));
    }

    @Test
    void isMatchFalseIfSourceIsNotRecord() {
        assertFalse(converter.match(Object.class, Object.class));
    }

}
