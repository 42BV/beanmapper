package io.beanmapper.testmodel.record;

import io.beanmapper.annotations.BeanRecordConstruct;
import io.beanmapper.annotations.BeanRecordConstructMode;

public record TargetRecordWithMultipleForcedConstructors(int id, String name) {

    @BeanRecordConstruct(value = "id", constructMode = BeanRecordConstructMode.FORCE)
    public TargetRecordWithMultipleForcedConstructors(int id) {
        this(id, "Henk");
    }

    @BeanRecordConstruct(value = "name", constructMode = BeanRecordConstructMode.FORCE)
    public TargetRecordWithMultipleForcedConstructors(String name) {
        this(1, name);
    }
}
