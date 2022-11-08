package io.beanmapper.testmodel.record;

import io.beanmapper.annotations.BeanRecordConstruct;
import io.beanmapper.annotations.BeanRecordConstructMode;

public record PersonResultRecord(int id, String name) {

    @BeanRecordConstruct(value = { "id", "name" }, constructMode = BeanRecordConstructMode.FORCE)
    public PersonResultRecord {
    }

}
