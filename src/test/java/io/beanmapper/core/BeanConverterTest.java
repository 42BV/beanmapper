package io.beanmapper.core;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class BeanConverterTest extends BeanConverter<LocalDateTime, LocalDate> {

    @Override
    public LocalDate from(LocalDateTime varFrom) {
        return varFrom.toLocalDate();
    }

    @Override
    public LocalDateTime to(LocalDate varTo) {
        return LocalDateTime.of(varTo.getYear(), varTo.getMonth(), varTo.getDayOfMonth(), 0, 0);
    }
}
