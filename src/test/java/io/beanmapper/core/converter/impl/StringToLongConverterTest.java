/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.converter.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StringToLongConverterTest {

    private StringToLongConverter converter;

    @BeforeEach
    void setUp() {
        converter = new StringToLongConverter();
    }

    @Test
    void testConvertBoxed() {
        assertTrue(converter.match(String.class, Long.class));
        assertEquals(Long.valueOf(42), converter.convert(null, "42", Long.class, null));
    }

    @Test
    void testConvertPrimitive() {
        assertTrue(converter.match(String.class, long.class));
        assertEquals(Long.valueOf(42), converter.convert(null, "42", long.class, null));
    }

}
