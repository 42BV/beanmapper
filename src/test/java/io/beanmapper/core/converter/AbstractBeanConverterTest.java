/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.converter;

import java.time.LocalDate;
import java.time.LocalDateTime;

import io.beanmapper.core.converter.impl.LocalDateTimeToLocalDate;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 *
 * @author Jeroen van Schagen
 * @since Jun 18, 2015
 */
public class AbstractBeanConverterTest {
    
    private LocalDateTimeToLocalDate converter = new LocalDateTimeToLocalDate();
    
    @Test
    public void testConvert() {
        LocalDateTime time = LocalDateTime.now();
        Assert.assertEquals(time.toLocalDate(), converter.convert(null,time, LocalDate.class, null));
    }
    
    @Test
    public void testNullSource() {
        Assert.assertNull(converter.convert(null,null, LocalDate.class, null));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidSource() {
        converter.convert(null,"Test", LocalDate.class, null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidTarget() {
        converter.convert(null,LocalDateTime.now(), String.class, null);
    }

}
