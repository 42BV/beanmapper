/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.converter.impl;

import org.junit.Assert;
import org.junit.Test;

public class ObjectToStringConverterTest {
    
    @Test
    public void testConvert() {
        Assert.assertEquals("42", new ObjectToStringConverter().convert(null,42, String.class, null));
    }

}
