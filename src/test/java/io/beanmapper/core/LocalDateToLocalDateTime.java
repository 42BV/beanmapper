package io.beanmapper.core;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LocalDateToLocalDateTime extends AbstractBeanConverter<LocalDate, LocalDateTime> {

    public LocalDateToLocalDateTime() {
        super(LocalDate.class, LocalDateTime.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime convert(LocalDate source) {
        return LocalDateTime.of(source.getYear(), source.getMonth(), source.getDayOfMonth(), 0, 0);
    }

}
