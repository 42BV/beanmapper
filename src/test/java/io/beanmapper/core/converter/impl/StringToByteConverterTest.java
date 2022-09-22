/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.converter.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StringToByteConverterTest {

    private StringToByteConverter converter;

    @BeforeEach
    void setUp() {
        converter = new StringToByteConverter();
    }

    @Test
    void testConvertBoxed() {
        assertTrue(converter.match(String.class, Byte.class));
        assertEquals((byte) 42, converter.convert(null, "42", Byte.class, null));
    }

    @Test
    void testConvertPrimitive() {
        assertTrue(converter.match(String.class, byte.class));
        assertEquals((byte) 42, converter.convert(null, "42", byte.class, null));
    }

}
