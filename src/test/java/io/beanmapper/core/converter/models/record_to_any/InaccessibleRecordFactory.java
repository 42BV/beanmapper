package io.beanmapper.core.converter.models.record_to_any;

public final class InaccessibleRecordFactory {

    private InaccessibleRecordFactory() {
    }

    public static Record create(String name) {
        return new InaccessibleRecord(name);
    }

    private record InaccessibleRecord(String name) {
    }

}
