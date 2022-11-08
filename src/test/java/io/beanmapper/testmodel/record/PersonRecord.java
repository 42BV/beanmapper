package io.beanmapper.testmodel.record;

import io.beanmapper.annotations.BeanRecordConstruct;

public record PersonRecord(int id, String name) {

    @BeanRecordConstruct(value = "name")
    public PersonRecord(String name) {
        this(1, name);
    }

    @BeanRecordConstruct(value = { "name", "id" })
    public PersonRecord(String name, int id) {
        this(id, name);
    }

}
