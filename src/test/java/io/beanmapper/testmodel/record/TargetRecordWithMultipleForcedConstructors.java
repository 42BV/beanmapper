package io.beanmapper.testmodel.record;

import io.beanmapper.annotations.RecordConstruct;
import io.beanmapper.annotations.RecordConstructMode;

public record TargetRecordWithMultipleForcedConstructors(int id, String name) {

    @RecordConstruct(value = "id", constructMode = RecordConstructMode.FORCE)
    public TargetRecordWithMultipleForcedConstructors(int id) {
        this(id, "Henk");
    }

    @RecordConstruct(value = "name", constructMode = RecordConstructMode.FORCE)
    public TargetRecordWithMultipleForcedConstructors(String name) {
        this(1, name);
    }
}
