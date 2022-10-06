package io.beanmapper.strategy.record.model.collection.form;

import java.util.ArrayDeque;
import java.util.Queue;

import io.beanmapper.strategy.record.model.FormWithIdAndName;

public class FormWithQueue extends FormWithIdAndName {

    public final Queue<String> numbers;

    public FormWithQueue(final int id, final String name, final Queue<String> numbers) {
        super(id, name);
        this.numbers = numbers;
    }

    public FormWithQueue() {
        this.numbers = new ArrayDeque<>() {
            {
                add("1");
                add("2");
                add("3");
            }
        };
    }

}
