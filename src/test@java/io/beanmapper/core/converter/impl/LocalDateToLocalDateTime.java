package io.beanmapper.core.converter.impl;

import io.beanmapper.core.converter.SimpleBeanConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LocalDateToLocalDateTime extends SimpleBeanConverter<LocalDate, LocalDateTime> {

    public LocalDateToLocalDateTime() {
        super(LocalDate.class, LocalDateTime.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime doConvert(LocalDate source) {
        return LocalDateTime.of(source.getYear(), source.getMonth(), source.getDayOfMonth(), 0, 0);
    }

}
