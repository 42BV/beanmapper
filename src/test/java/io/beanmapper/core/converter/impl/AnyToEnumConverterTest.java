/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.converter.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AnyToEnumConverterTest {

    private AnyToEnumConverter converter;

    @BeforeEach
    void setUp() {
        converter = new AnyToEnumConverter();
    }

    @Test
    void testWithName() {
        assertEquals(TestEnum.B, converter.convert(null, "B", TestEnum.class, null));
    }

    @Test
    void testWithEmptyName() {
        assertNull(converter.convert(null, "   ", TestEnum.class, null));
    }

    @Test
    void testWithUnknownName() {
        assertThrows(IllegalArgumentException.class, () -> converter.convert(null, "?", TestEnum.class, null));
    }

    @Test
    void testEnumToEnum() {
        TargetSideEnum target = (TargetSideEnum) converter.convert(null, SourceSideEnum.ALPHA, TargetSideEnum.class, null);
        assertEquals(TargetSideEnum.ALPHA, target);
    }

    @Test
    void objectToEnum() {
        NestedString source = new NestedString("ALPHA");
        TargetSideEnum target = (TargetSideEnum) converter.convert(null, source, TargetSideEnum.class, null);
        assertEquals(TargetSideEnum.ALPHA, target);
    }

    public enum TestEnum {
        A, B, C;
    }

    public enum SourceSideEnum {
        ALPHA
    }

    public enum TargetSideEnum {
        ALPHA
    }

    public class NestedString {
        private final String name;

        public NestedString(String name) {
            this.name = name;
        }

        public String toString() {
            return name;
        }
    }

}
