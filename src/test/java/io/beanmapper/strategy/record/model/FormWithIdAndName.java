package io.beanmapper.strategy.record.model;

public class FormWithIdAndName extends FormWithName {
    public final int id;

    public FormWithIdAndName(final int id, final String name) {
        super(name);
        this.id = id;
    }

    public FormWithIdAndName() {
        this(42, "Henk");
    }
}
