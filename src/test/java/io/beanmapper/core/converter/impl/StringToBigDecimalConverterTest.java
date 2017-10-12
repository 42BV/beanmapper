/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.converter.impl;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StringToBigDecimalConverterTest {
    
    private StringToBigDecimalConverter converter;
    
    @Before
    public void setUp() {
        converter = new StringToBigDecimalConverter();
    }
    
    @Test
    public void testConvert() {
        Assert.assertEquals("42.24", converter.convert(null,"42.24", BigDecimal.class, null).toString());
    }

}
