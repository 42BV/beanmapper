/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.converter.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ObjectToStringConverterTest {

    @Test
    void testConvert() {
        assertEquals("42", new ObjectToStringConverter().convert(null, 42, String.class, null));
    }

}
