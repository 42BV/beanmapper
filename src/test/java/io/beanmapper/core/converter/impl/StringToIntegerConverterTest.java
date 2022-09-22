/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.converter.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StringToIntegerConverterTest {

    private StringToIntegerConverter converter;

    @BeforeEach
    void setUp() {
        converter = new StringToIntegerConverter();
    }

    @Test
    void testConvertBoxed() {
        assertTrue(converter.match(String.class, Integer.class));
        assertEquals(42, converter.convert(null, "42", Integer.class, null));
    }

    @Test
    void testConvertPrimitive() {
        assertTrue(converter.match(String.class, int.class));
        assertEquals(42, converter.convert(null, "42", int.class, null));
    }

}
