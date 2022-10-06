package io.beanmapper.strategy.record.model.annotation.record_construct;

import io.beanmapper.annotations.BeanRecordConstruct;
import io.beanmapper.annotations.BeanRecordConstructMode;

public record RecordWithMultipleForcedConstructors(int id, String name) {

    @BeanRecordConstruct(constructMode = BeanRecordConstructMode.FORCE)
    public RecordWithMultipleForcedConstructors {
    }

    @BeanRecordConstruct(constructMode = BeanRecordConstructMode.FORCE)
    public RecordWithMultipleForcedConstructors() {
        this(42, "Henk");
    }

}
