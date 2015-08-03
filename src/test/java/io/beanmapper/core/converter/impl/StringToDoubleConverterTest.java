/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.converter.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StringToDoubleConverterTest {
    
    private StringToDoubleConverter converter;
    
    @Before
    public void setUp() {
        converter = new StringToDoubleConverter();
    }
    
    @Test
    public void testConvertBoxed() {
        Assert.assertTrue(converter.match(String.class, Double.class));
        Assert.assertEquals(Double.valueOf((double) 42), converter.convert("42", Double.class, null));
    }
    
    @Test
    public void testConvertPrimitive() {
        Assert.assertTrue(converter.match(String.class, double.class));
        Assert.assertEquals(Double.valueOf((double) 42), converter.convert("42", double.class, null));
    }

}
