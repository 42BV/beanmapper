package io.beanmapper.testmodel.record;

import io.beanmapper.annotations.RecordConstruct;

public record PersonRecord(int id, String name) {

    @RecordConstruct(value = "name")
    public PersonRecord(String name) {
        this(1, name);
    }

    @RecordConstruct(value = {"name", "id"})
    public PersonRecord(String name, int id) {
        this(id, name);
    }

}
