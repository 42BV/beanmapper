/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;

import io.beanmapper.core.converter.impl.LocalDateTimeToLocalDate;

import org.junit.jupiter.api.Test;

/**
 * @author Jeroen van Schagen
 * @since Jun 18, 2015
 */
class AbstractBeanConverterTest {

    private LocalDateTimeToLocalDate converter = new LocalDateTimeToLocalDate();

    @Test
    void testConvert() {
        LocalDateTime time = LocalDateTime.now();
        assertEquals(time.toLocalDate(), converter.convert(null, time, LocalDate.class, null));
    }

    @Test
    void testNullSource() {
        assertNull(converter.convert(null, null, LocalDate.class, null));
    }

    @Test
    void testInvalidSource() {
        assertThrows(IllegalArgumentException.class, () -> converter.convert(null, "Test", LocalDate.class, null));
    }

    @Test
    void testInvalidTarget() {
        LocalDateTime time = LocalDateTime.now();
        assertThrows(IllegalArgumentException.class, () -> converter.convert(null, time, String.class, null));
    }

}
