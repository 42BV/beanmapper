/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.converter.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StringToShortConverterTest {

    private StringToShortConverter converter;

    @BeforeEach
    void setUp() {
        converter = new StringToShortConverter();
    }

    @Test
    void testConvertBoxed() {
        assertTrue(converter.match(String.class, Short.class));
        assertEquals(Short.valueOf((short) 42), converter.convert(null, "42", Short.class, null));
    }

    @Test
    void testConvertPrimitive() {
        assertTrue(converter.match(String.class, short.class));
        assertEquals(Short.valueOf((short) 42), converter.convert(null, "42", short.class, null));
    }

}
