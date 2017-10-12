package io.beanmapper.core.converter.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;

import io.beanmapper.core.converter.SimpleBeanConverter;

public class LocalDateTimeToLocalDate extends SimpleBeanConverter<LocalDateTime, LocalDate> {

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDate doConvert(LocalDateTime source) {
        return source.toLocalDate();
    }

}
