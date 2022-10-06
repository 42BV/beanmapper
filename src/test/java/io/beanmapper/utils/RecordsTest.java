package io.beanmapper.utils;

import static org.junit.jupiter.api.Assertions.*;

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
        assertEquals(assertDoesNotThrow(() -> PersonRecord.class.getConstructor(String.class, int.class)), constructors.get(0));
        assertEquals(assertDoesNotThrow(() -> PersonRecord.class.getConstructor(String.class)), constructors.get(1));
    }

}