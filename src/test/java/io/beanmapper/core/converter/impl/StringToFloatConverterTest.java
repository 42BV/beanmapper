/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.converter.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StringToFloatConverterTest {

    private StringToFloatConverter converter;

    @BeforeEach
    void setUp() {
        converter = new StringToFloatConverter();
    }

    @Test
    void testConvertBoxed() {
        assertTrue(converter.match(String.class, Float.class));
        assertEquals(Float.valueOf((float) 42), converter.convert(null, "42", Float.class, null));
    }

    @Test
    void testConvertPrimitive() {
        assertTrue(converter.match(String.class, float.class));
        assertEquals(Float.valueOf((float) 42), converter.convert(null, "42", float.class, null));
    }

}
