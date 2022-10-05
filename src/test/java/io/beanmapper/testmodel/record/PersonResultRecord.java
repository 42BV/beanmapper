package io.beanmapper.testmodel.record;

import io.beanmapper.annotations.RecordConstruct;
import io.beanmapper.annotations.RecordConstructMode;

public record PersonResultRecord(int id, String name) {

    @RecordConstruct(value = { "id", "name" }, constructMode = RecordConstructMode.FORCE)
    public PersonResultRecord {
    }

}
