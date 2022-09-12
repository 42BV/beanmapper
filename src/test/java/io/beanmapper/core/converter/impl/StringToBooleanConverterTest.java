/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.converter.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StringToBooleanConverterTest {

    private StringToBooleanConverter converter;

    @BeforeEach
    void setUp() {
        converter = new StringToBooleanConverter();
    }

    @Test
    void testConvertBoxed() {
        assertTrue(converter.match(String.class, Boolean.class));
        assertEquals(Boolean.TRUE, converter.convert(null, "true", Boolean.class, null));
    }

    @Test
    void testConvertPrimitive() {
        assertTrue(converter.match(String.class, boolean.class));
        assertEquals(Boolean.TRUE, converter.convert(null, "true", boolean.class, null));
    }

}
