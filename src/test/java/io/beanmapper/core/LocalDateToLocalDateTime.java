package io.beanmapper.core;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LocalDateToLocalDateTime extends AbstractBeanConverter<LocalDate, LocalDateTime> {

    @Override
    public LocalDateTime convert(LocalDate source) {
        return LocalDateTime.of(source.getYear(), source.getMonth(), source.getDayOfMonth(), 0, 0);
    }

    @Override
    public Class<LocalDate> getSourceClass() {
        return LocalDate.class;
    }

    @Override
    public Class<LocalDateTime> getTargetClass() {
        return LocalDateTime.class;
    }

}
