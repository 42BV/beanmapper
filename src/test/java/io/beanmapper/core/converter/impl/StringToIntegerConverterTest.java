/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.converter.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StringToIntegerConverterTest {
    
    private StringToIntegerConverter converter;
    
    @Before
    public void setUp() {
        converter = new StringToIntegerConverter();
    }
    
    @Test
    public void testConvertBoxed() {
        Assert.assertTrue(converter.match(String.class, Integer.class));
        Assert.assertEquals(Integer.valueOf(42), converter.convert(null,"42", Integer.class, null));
    }
    
    @Test
    public void testConvertPrimitive() {
        Assert.assertTrue(converter.match(String.class, int.class));
        Assert.assertEquals(Integer.valueOf(42), converter.convert(null,"42", int.class, null));
    }

}
