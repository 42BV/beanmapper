package io.beanmapper.core.converter.models.record_to_any;

public record ThrowingAccessorRecord(String name) {

    @Override
    public String name() {
        throw new IllegalStateException("Accessor failed");
    }

}
