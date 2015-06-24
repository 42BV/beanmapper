/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.converter.impl;

import io.beanmapper.BeanMapper;

import java.math.BigDecimal;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class NumberToNumberConverterTest {
    
    private BeanMapper beanMapper;
    
    private NumberToNumberConverter converter;
    
    @Before
    public void setUp() {
        beanMapper = new BeanMapper();
        converter = new NumberToNumberConverter(beanMapper);
    }
    
    @Test
    public void testIntegerToLong() {
        Assert.assertTrue(converter.match(Integer.class, Long.class));
        Assert.assertEquals(Long.valueOf(42), converter.convert(Integer.valueOf(42), Long.class));
    }
    
    @Test
    public void testLongToPrimitiveInteger() {
        Assert.assertTrue(converter.match(Long.class, int.class));
        Assert.assertEquals(Integer.valueOf(42), converter.convert(Long.valueOf(42), int.class));
    }
    
    @Test
    public void testDoubleToBigDecimal() {
        Assert.assertTrue(converter.match(double.class, BigDecimal.class));
        Assert.assertEquals("42.24", converter.convert(42.24D, BigDecimal.class).toString());
    }

}
