package io.beanmapper.core;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LocalDateTimeToLocalDate extends AbstractBeanConverter<LocalDateTime, LocalDate> {

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDate convert(LocalDateTime source) {
        return source.toLocalDate();
    }

}
