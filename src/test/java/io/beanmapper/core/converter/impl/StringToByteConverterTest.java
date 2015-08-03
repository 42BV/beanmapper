/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.converter.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StringToByteConverterTest {
    
    private StringToByteConverter converter;
    
    @Before
    public void setUp() {
        converter = new StringToByteConverter();
    }
    
    @Test
    public void testConvertBoxed() {
        Assert.assertTrue(converter.match(String.class, Byte.class));
        Assert.assertEquals(Byte.valueOf((byte) 42), converter.convert("42", Byte.class, null));
    }
    
    @Test
    public void testConvertPrimitive() {
        Assert.assertTrue(converter.match(String.class, byte.class));
        Assert.assertEquals(Byte.valueOf((byte) 42), converter.convert("42", byte.class, null));
    }

}
