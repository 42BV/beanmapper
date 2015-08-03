/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.converter.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StringToEnumConverterTest {
    
    private StringToEnumConverter converter;
    
    @Before
    public void setUp() {
        converter = new StringToEnumConverter();
    }
    
    @Test
    public void testWithName() {
        Assert.assertEquals(TestEnum.B, converter.convert("B", TestEnum.class, null));
    }
    
    @Test
    public void testWithEmptyName() {
        Assert.assertNull(converter.convert("   ", TestEnum.class, null));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testWithUnknownName() {
        converter.convert("?", TestEnum.class, null);
    }
    
    public enum TestEnum {
        A, B, C;
    }

}
