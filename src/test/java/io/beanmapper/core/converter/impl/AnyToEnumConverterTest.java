/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.converter.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AnyToEnumConverterTest {
    
    private AnyToEnumConverter converter;
    
    @Before
    public void setUp() {
        converter = new AnyToEnumConverter();
    }
    
    @Test
    public void testWithName() {
        assertEquals(TestEnum.B, converter.convert(null,"B", TestEnum.class, null));
    }
    
    @Test
    public void testWithEmptyName() {
        Assert.assertNull(converter.convert(null,"   ", TestEnum.class, null));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testWithUnknownName() {
        converter.convert(null,"?", TestEnum.class, null);
    }

    @Test
    public void testEnumToEnum() {
        TargetSideEnum target = (TargetSideEnum)converter.convert(null, SourceSideEnum.ALPHA, TargetSideEnum.class, null);
        assertEquals(TargetSideEnum.ALPHA, target);
    }

    @Test
    public void objectToEnum() {
        NestedString source = new NestedString("ALPHA");
        TargetSideEnum target = (TargetSideEnum)converter.convert(null, source, TargetSideEnum.class, null);
        assertEquals(TargetSideEnum.ALPHA, target);
    }

    public enum TestEnum {
        A, B, C;
    }

    public enum SourceSideEnum {
        ALPHA
    }

    public enum TargetSideEnum {
        ALPHA
    }

    public class NestedString {
        private final String name;

        public NestedString(String name) {
            this.name = name;
        }

        public String toString() {
            return name;
        }
    }

}
