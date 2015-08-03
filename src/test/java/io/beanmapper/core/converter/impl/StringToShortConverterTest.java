/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.converter.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StringToShortConverterTest {
    
    private StringToShortConverter converter;
    
    @Before
    public void setUp() {
        converter = new StringToShortConverter();
    }
    
    @Test
    public void testConvertBoxed() {
        Assert.assertTrue(converter.match(String.class, Short.class));
        Assert.assertEquals(Short.valueOf((short) 42), converter.convert("42", Short.class, null));
    }
    
    @Test
    public void testConvertPrimitive() {
        Assert.assertTrue(converter.match(String.class, short.class));
        Assert.assertEquals(Short.valueOf((short) 42), converter.convert("42", short.class, null));
    }

}
