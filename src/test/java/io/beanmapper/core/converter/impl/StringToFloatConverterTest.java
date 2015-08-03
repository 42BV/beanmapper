/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.converter.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StringToFloatConverterTest {
    
    private StringToFloatConverter converter;
    
    @Before
    public void setUp() {
        converter = new StringToFloatConverter();
    }
    
    @Test
    public void testConvertBoxed() {
        Assert.assertTrue(converter.match(String.class, Float.class));
        Assert.assertEquals(Float.valueOf((float) 42), converter.convert("42", Float.class, null));
    }
    
    @Test
    public void testConvertPrimitive() {
        Assert.assertTrue(converter.match(String.class, float.class));
        Assert.assertEquals(Float.valueOf((float) 42), converter.convert("42", float.class, null));
    }

}
