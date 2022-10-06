package io.beanmapper.strategy.record.model.annotation.bean_alias.form;

import java.util.List;

import io.beanmapper.annotations.BeanAlias;

public class FormWithList {

    @BeanAlias("name")
    public final String abc;
    @BeanAlias("id")
    public final int def;
    @BeanAlias("numbers")
    public final List<String> ghi;

    public FormWithList(final int id, final String name, final List<String> numbers) {
        this.def = id;
        this.abc = name;
        this.ghi = numbers;
    }

    public FormWithList() {
        this(42, "Henk", List.of("1", "2", "3"));
    }

}
