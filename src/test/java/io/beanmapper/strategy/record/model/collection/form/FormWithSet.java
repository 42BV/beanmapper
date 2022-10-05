package io.beanmapper.strategy.record.model.collection.form;

import java.util.Set;

import io.beanmapper.strategy.record.model.FormWithIdAndName;

public class FormWithSet extends FormWithIdAndName {

    public final Set<String> numbers;

    public FormWithSet(final int id, final String name, final Set<String> numbers) {
        super(id, name);
        this.numbers = numbers;
    }

    public FormWithSet() {
        this.numbers = Set.of("1", "2", "3");
    }

}
