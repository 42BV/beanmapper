/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    public void testNullSource() {
        Assert.assertNull(converter.convert(null, LocalDate.class));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidSource() {
        converter.convert("Test", LocalDate.class);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidTarget() {
        converter.convert(LocalDateTime.now(), String.class);
    }

}
