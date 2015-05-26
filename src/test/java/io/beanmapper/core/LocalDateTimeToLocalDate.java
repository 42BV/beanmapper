package io.beanmapper.core;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LocalDateTimeToLocalDate extends AbstractBeanConverter<LocalDateTime, LocalDate> {

    @Override
    public LocalDate convert(LocalDateTime source) {
        return source.toLocalDate();
    }

    @Override
    public Class<LocalDateTime> getSourceClass() {
        return LocalDateTime.class;
    }

    @Override
    public Class<LocalDate> getTargetClass() {
        return LocalDate.class;
    }

}
