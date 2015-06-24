/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.converter;

import org.junit.Assert;
import org.junit.Test;

public class ToStringConverterTest {
    
    @Test
    public void testConvert() {
        Assert.assertEquals("42", new ToStringConverter().convert(42, String.class));
    }

}
