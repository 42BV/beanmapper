package io.beanmapper.testmodel.tostring;

import java.time.LocalDate;

public class SourceWithNonString {

    private LocalDate date;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
