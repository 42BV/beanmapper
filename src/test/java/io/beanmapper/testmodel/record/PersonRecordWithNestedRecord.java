package io.beanmapper.testmodel.record;

public record PersonRecordWithNestedRecord(int id, NestedRecord result) {

    public record NestedRecord(String name) {}

}
