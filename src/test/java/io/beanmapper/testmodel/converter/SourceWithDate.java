package io.beanmapper.testmodel.converter;

import java.time.LocalDate;

public class SourceWithDate {

    private LocalDate diffType;
    private LocalDate sameType;

    public LocalDate getDiffType() {
        return diffType;
    }

    public void setDiffType(LocalDate diffType) {
        this.diffType = diffType;
    }

    public LocalDate getSameType() {
        return sameType;
    }

    public void setSameType(LocalDate sameType) {
        this.sameType = sameType;
    }
}
