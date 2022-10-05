package io.beanmapper.strategy.record.model.annotation.record_construct;

import io.beanmapper.annotations.RecordConstruct;
import io.beanmapper.annotations.RecordConstructMode;

public record RecordWithMultipleForcedConstructors(int id, String name) {

    @RecordConstruct(constructMode = RecordConstructMode.FORCE)
    public RecordWithMultipleForcedConstructors {
    }

    @RecordConstruct(constructMode = RecordConstructMode.FORCE)
    public RecordWithMultipleForcedConstructors() {
        this(42, "Henk");
    }
    
}
