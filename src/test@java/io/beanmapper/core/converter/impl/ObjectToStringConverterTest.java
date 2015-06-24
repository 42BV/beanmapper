/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.converter.impl;

import io.beanmapper.core.converter.impl.ObjectToStringConverter;

import org.junit.Assert;
import org.junit.Test;

public class ObjectToStringConverterTest {
    
    @Test
    public void testConvert() {
        Assert.assertEquals("42", new ObjectToStringConverter().convert(42, String.class));
    }

}
