package io.beanmapper.strategy.record.model.annotation.record_construct;

import io.beanmapper.annotations.RecordConstruct;
import io.beanmapper.annotations.RecordConstructMode;

public record RecordWithAllConstructorsExcluded(int id, String name) {

    @RecordConstruct(constructMode = RecordConstructMode.EXCLUDE)
    public RecordWithAllConstructorsExcluded {
    }

}
