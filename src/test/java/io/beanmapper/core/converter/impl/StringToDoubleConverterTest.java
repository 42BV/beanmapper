/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.converter.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StringToDoubleConverterTest {

    private StringToDoubleConverter converter;

    @BeforeEach
    void setUp() {
        converter = new StringToDoubleConverter();
    }

    @Test
    void testConvertBoxed() {
        assertTrue(converter.match(String.class, Double.class));
        assertEquals((double) 42, converter.convert(null, "42", Double.class, null));
    }

    @Test
    void testConvertPrimitive() {
        assertTrue(converter.match(String.class, double.class));
        assertEquals((double) 42, converter.convert(null, "42", double.class, null));
    }

}
