/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.converter.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StringToBigDecimalConverterTest {

    private StringToBigDecimalConverter converter;

    @BeforeEach
    void setUp() {
        converter = new StringToBigDecimalConverter();
    }

    @Test
    void testConvert() {
        assertEquals("42.24", converter.convert(null, "42.24", BigDecimal.class, null).toString());
    }

}
