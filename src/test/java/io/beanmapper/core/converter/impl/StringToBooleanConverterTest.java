/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.converter.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StringToBooleanConverterTest {
    
    private StringToBooleanConverter converter;
    
    @Before
    public void setUp() {
        converter = new StringToBooleanConverter();
    }
    
    @Test
    public void testConvertBoxed() {
        Assert.assertTrue(converter.match(String.class, Boolean.class));
        Assert.assertEquals(Boolean.TRUE, converter.convert("true", Boolean.class));
    }
    
    @Test
    public void testConvertPrimitive() {
        Assert.assertTrue(converter.match(String.class, boolean.class));
        Assert.assertEquals(Boolean.TRUE, converter.convert("true", boolean.class));
    }

}
