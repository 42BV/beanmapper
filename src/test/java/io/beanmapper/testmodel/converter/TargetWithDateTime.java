package io.beanmapper.testmodel.converter;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TargetWithDateTime {

    private LocalDateTime diffType;
    private LocalDate sameType;

    public LocalDateTime getDiffType() {
        return diffType;
    }

    public void setDiffType(LocalDateTime diffType) {
        this.diffType = diffType;
    }

    public LocalDate getSameType() {
        return sameType;
    }

    public void setSameType(LocalDate sameType) {
        this.sameType = sameType;
    }
}
