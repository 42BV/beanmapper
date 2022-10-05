package io.beanmapper.testmodel.record;

import io.beanmapper.annotations.RecordConstruct;
import io.beanmapper.annotations.RecordConstructMode;

public record TargetRecordAllowsNull(String name, int id, int age) {

    @RecordConstruct(value = { "name", "id", "age" }, allowNull = true, constructMode = RecordConstructMode.FORCE)
    public TargetRecordAllowsNull {
    }

}
