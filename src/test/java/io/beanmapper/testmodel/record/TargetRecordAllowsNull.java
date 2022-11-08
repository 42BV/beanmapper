package io.beanmapper.testmodel.record;

import io.beanmapper.annotations.BeanRecordConstruct;
import io.beanmapper.annotations.BeanRecordConstructMode;

public record TargetRecordAllowsNull(String name, int id, int age) {

    @BeanRecordConstruct(value = { "name", "id", "age" }, allowNull = true, constructMode = BeanRecordConstructMode.FORCE)
    public TargetRecordAllowsNull {
    }

}
