package io.beanmapper.testmodel.record;

import java.time.LocalDate;

public class SourceClassPrivate {
    private Integer i;
    private String s;
    private LocalDate d;

    public SourceClassPrivate(Integer i, String s, LocalDate d) {
        this.i = i;
        this.s = s;
        this.d = d;
    }
}
