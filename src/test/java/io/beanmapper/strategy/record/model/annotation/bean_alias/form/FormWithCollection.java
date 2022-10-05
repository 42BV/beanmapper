package io.beanmapper.strategy.record.model.annotation.bean_alias.form;

import java.util.Collection;
import java.util.List;

import io.beanmapper.annotations.BeanAlias;

public class FormWithCollection {

    @BeanAlias("name")
    public final String abc;
    @BeanAlias("id")
    public final int def;
    @BeanAlias("numbers")
    public final Collection<String> ghi;

    public FormWithCollection(final int id, final String name, final Collection<String> numbers) {
        this.abc = name;
        this.def = id;
        this.ghi = numbers;
    }

    public FormWithCollection() {
        this(42, "Henk", List.of("1", "2", "3"));
    }

}
