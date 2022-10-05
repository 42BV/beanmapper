package io.beanmapper.strategy.record.model.collection.form;

import java.util.List;

import io.beanmapper.strategy.record.model.FormWithIdAndName;

public class FormWithList extends FormWithIdAndName {

    public final List<String> numbers;

    public FormWithList(final int id, final String name, final List<String> numbers) {
        super(id, name);
        this.numbers = numbers;
    }

    public FormWithList() {
        this.numbers = List.of("1", "2", "3");
    }

}
