package io.beanmapper.strategy.record.model.annotation.record_construct;

import io.beanmapper.annotations.BeanRecordConstruct;
import io.beanmapper.annotations.BeanRecordConstructMode;

public record RecordWithAllConstructorsExcluded(int id, String name) {

    @BeanRecordConstruct(constructMode = BeanRecordConstructMode.EXCLUDE)
    public RecordWithAllConstructorsExcluded {
    }

}
