package io.beanmapper.strategy.record.model.collection.form;

import java.util.Map;

import io.beanmapper.strategy.record.model.FormWithIdAndName;

public class FormWithMap extends FormWithIdAndName {

    public final Map<Integer, String> numbers;

    public FormWithMap(final int id, final String name, final Map<Integer, String> numbers) {
        super(id, name);
        this.numbers = numbers;
    }

    public FormWithMap() {
        this.numbers = Map.of(1, "1", 2, "2", 3, "3");
    }

}
