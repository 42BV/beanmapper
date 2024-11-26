package io.beanmapper.utils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.util.List;

import io.beanmapper.testmodel.record.PersonRecord;

import org.junit.jupiter.api.Test;

class RecordsTest {

    @Test
    void testGetRecordFieldNames() {
        var result = Records.getRecordFieldNames(PersonRecord.class);
        assertEquals(2, result.length);
        assertEquals("id", result[0]);
        assertEquals("name", result[1]);
    }

    @Test
    void testGetCanonicalConstructorOfRecord() {
        var canonicalConstructor = assertDoesNotThrow(() -> Records.getCanonicalConstructorOfRecord(PersonRecord.class));
        assertEquals(assertDoesNotThrow(() -> PersonRecord.class.getConstructor(int.class, String.class)), canonicalConstructor);
    }

    @Test
    void testGetRecordConstructAnnotatedConstructors() {
        List<Constructor<PersonRecord>> constructors = (List<Constructor<PersonRecord>>) Records.getConstructorsAnnotatedWithRecordConstruct(PersonRecord.class);
        assertTrue(assertDoesNotThrow(() -> constructors.contains(PersonRecord.class.getConstructor(String.class, int.class))));
        assertTrue(assertDoesNotThrow(() -> constructors.contains(PersonRecord.class.getConstructor(String.class))));
    }
}