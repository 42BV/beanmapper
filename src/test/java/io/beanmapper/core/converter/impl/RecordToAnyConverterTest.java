package io.beanmapper.core.converter.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import io.beanmapper.core.converter.models.record_to_any.Intermediary;
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

    @Test
    void testCopyFieldsToIntermediaryWithNullValueInMap() {
        var intermediary = new Intermediary();
        var copyFieldsToIntermediary = assertDoesNotThrow(() -> this.converter.getClass().getDeclaredMethod("copyFieldsToIntermediary", Object.class, Map.class));
        copyFieldsToIntermediary.setAccessible(true);
        var map = new HashMap<>();
        map.put("name", null);
        assertDoesNotThrow(() -> copyFieldsToIntermediary.invoke(this.converter, intermediary, map));
    }

    @Test
    void testCopyFieldToIntermediaryWithNullKeyInMapShouldThrowInvocationTargetException() {
        var intermediary = new Intermediary();
        var copyFieldToIntermediary = assertDoesNotThrow(() -> this.converter.getClass().getDeclaredMethod("copyFieldsToIntermediary", Object.class, Map.class));
        copyFieldToIntermediary.setAccessible(true);
        var map = new HashMap<>();
        map.put(null, "");
        var exception = assertThrows(InvocationTargetException.class, () -> copyFieldToIntermediary.invoke(this.converter, intermediary, map));
        assertEquals(NullPointerException.class, exception.getCause().getClass());
    }

}
