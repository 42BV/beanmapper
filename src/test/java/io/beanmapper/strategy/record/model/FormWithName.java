package io.beanmapper.strategy.record.model;

public class FormWithName {

    public final String name;

    public FormWithName(final String name) {
        this.name = name;
    }

    public FormWithName() {
        this("Henk");
    }

}
