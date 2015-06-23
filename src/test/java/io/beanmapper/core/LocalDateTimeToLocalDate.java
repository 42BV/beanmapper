package io.beanmapper.core;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LocalDateTimeToLocalDate extends SimpleBeanConverter<LocalDateTime, LocalDate> {

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDate doConvert(LocalDateTime source) {
        return source.toLocalDate();
    }

}
