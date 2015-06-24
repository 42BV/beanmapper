/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.converter.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StringToLongConverterTest {
    
    private StringToLongConverter converter;
    
    @Before
    public void setUp() {
        converter = new StringToLongConverter();
    }
    
    @Test
    public void testConvertBoxed() {
        Assert.assertTrue(converter.match(String.class, Long.class));
        Assert.assertEquals(Long.valueOf(42), converter.convert("42", Long.class));
    }
    
    @Test
    public void testConvertPrimitive() {
        Assert.assertTrue(converter.match(String.class, long.class));
        Assert.assertEquals(Long.valueOf(42), converter.convert("42", long.class));
    }

}
